package org.levigo.jadice.server.converterclient.gui.inspector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;
import org.levigo.jadice.server.converterclient.JobCard;
import org.levigo.jadice.server.converterclient.JobCardFactory;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;
import org.levigo.jadice.server.converterclient.gui.ComponentWrapper;
import org.levigo.jadice.server.converterclient.gui.inspector.WorkflowLayout.Type;
import org.levigo.jadice.server.converterclient.util.UiUtil;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.Node;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class ConfigurationInspectorPaneController implements NodeSelectionListener {

	private static final Logger LOGGER = Logger.getLogger(ConfigurationInspectorPaneController.class);
	
	private static final String[] HIDDEN_NODE_PROPERTY_NAMES = new String[] {//
	    "UUID", "job", "limits", "nodeLimits",//
	    "inputCardinality", "outputCardinality", "streamBundle", //
	    "predecessors", "successors", "subsidiaryNodes"
  };
	  
	private static final Set<String> HIDDEN_NODE_PROPERTY_NAMES_SET = new HashSet<>(Arrays.asList(HIDDEN_NODE_PROPERTY_NAMES));
	
	@FXML
	private ComboBox<WorkflowConfiguration> configurations;
	
	@FXML
	private ComboBox<WorkflowLayout.Type> layouts;
	
	@FXML
	private Button home;
	
	@FXML
	private Button exportJava;
	
	@FXML
	MasterDetailPane masterDetailPane;
	
	@FXML
	private BorderPane displayPane;
	
	@FXML
	private PropertySheet propertySheet;
	
	private final WorkflowDisplay display = new WorkflowDisplay();
	
  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    initConfigurationCB();
		
		final ObservableList<Type> layoutValues = FXCollections.observableArrayList(WorkflowLayout.Type.values());
		layouts.setItems(layoutValues);
    layouts.setCellFactory(ComboBoxListCell.forListView(layoutValues));
		layouts.valueProperty().addListener((observable, oldValue, newValue) -> {
		  SwingUtilities.invokeLater(() -> {
		    display.setGraphLayout(newValue);
		  });
		});
		layouts.setValue(layouts.getItems().get(0));

    display.addSelectionListener(this);

    displayPane.setCenter(new ComponentWrapper<>(display));
    
    masterDetailPane.showDetailNodeProperty().bind(new SimpleListProperty<>(propertySheet.getItems()).emptyProperty().not());
	}
  
  @FXML
  protected void exportJava() {
    final String java = CodeGenerator.exportJavaImplementation(display.getJob());
    LOGGER.info("Created java code:\n" + java);
  }

  private void initConfigurationCB() {
	  configurations.itemsProperty().bind(new SimpleListProperty<>(JobCardFactory.getInstance().getConfigurations()));
    final StringConverter<WorkflowConfiguration> sc = new StringConverter<WorkflowConfiguration>() {
      @Override
      public String toString(WorkflowConfiguration object) {
        return String.format("%s [%s]", object.getDescription(), object.getID());
      }

      @Override
      public WorkflowConfiguration fromString(String string) {
        return null;
      }
    };
    configurations.setCellFactory(ComboBoxListCell.forListView(sc, configurations.getItems()));
    configurations.setButtonCell(new ComboBoxListCell<>(sc, configurations.getItems()));
    configurations.setValue(configurations.getItems().get(0));

    configurations.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        clearGraph();
        return;
      }
      try {
        showGraph(newValue);
      } catch (Exception e) {
        LOGGER.error("Error when showing workflow configuration", e);
      }
    });
	}

	private void clearGraph() {
		display.clear();
	}
	
	public void showGraph(JobCard jobCard) {
	  SwingUtilities.invokeLater(() -> {
	    display.showJob(jobCard.job);
	  });
	}

  private void showGraph(WorkflowConfiguration item) throws Exception {
    final Job dummyJob = new DummyJob();
    item.configureWorkflow(dummyJob);
    SwingUtilities.invokeLater(() -> {
      display.showJob(dummyJob);
    });
  }

	public void deselected(Node deselected) {
		LOGGER.debug("DESELECTED: " + deselected);
		Platform.runLater(() -> {
      propertySheet.getItems().clear();
    }); 
	}

	public void selected(final Node selected) {
	  LOGGER.debug("SELECTED: " + selected);
	  final ObservableList<Item> properties = BeanPropertyUtils.getProperties(selected, item -> !HIDDEN_NODE_PROPERTY_NAMES_SET.contains(item.getName()));
	  properties.stream().forEach(item -> {
	    try {
	      item.getValue();
	    } catch (Exception e) {
	      LOGGER.error("Could not read property " + item.getName(), e);
	    }
	  });

	  Platform.runLater(() -> {
	    propertySheet.getItems().clear();
	    propertySheet.getItems().addAll(properties);
	  }) ; 
	}
}

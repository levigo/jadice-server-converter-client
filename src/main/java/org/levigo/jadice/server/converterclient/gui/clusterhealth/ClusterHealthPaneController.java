package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.controlsfx.control.GridView;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.ClusterHealthDTO;
import org.levigo.jadice.server.converterclient.util.FxScheduler;
import org.levigo.jadice.server.converterclient.util.UiUtil;

import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ClusterHealthPaneController {
  
  private static final Logger LOGGER = Logger.getLogger(ClusterHealthPaneController.class);

  @FXML
  private Button home;

  @FXML
  private GridView<StatusControl> gridView;
  
  @FXML
  private ToggleButton toggleSettingsButton;
  
  @FXML
  private Button addInstance;
  
  @FXML
  private  HiddenSidesPane hiddenSidePane;
  
  @FXML
  private Button defineWarnings;

  @FXML
  private ResourceBundle resources;

  private final ExecutorService exec = Executors.newWorkStealingPool();
  
  private final ClusterHealthDTO settings = Preferences.clusterHealthProperty().getValue();

  private final ObservableList<StatusControl> controlElements = FXCollections.observableArrayList();
  
  private FxScheduler timer;
  
  private PopOver addInstancePopover;
  
  private PopOver defineWarningsPopover;

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    
    loadControlElements();
    defineWarningsPopover = initPopoverButton(defineWarnings, "/fxml/ConfigureClusterHealthWarnings.fxml");
    addInstancePopover = initPopoverButton(addInstance, "/fxml/AddClusterInstance.fxml");
    
    hiddenSidePane.pinnedSideProperty().bind(new When(toggleSettingsButton.selectedProperty()).then(Side.TOP).otherwise((Side) null));
    
    gridView.setCellFactory(view -> new StatusControlGridCell());
    gridView.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    gridView.setItems(controlElements);
    
    timer = new FxScheduler(this::runUpdate);
    timer.setExecutionUnit(TimeUnit.MINUTES); // Unit is not configurable yet
    timer.executionRateProperty().bind(Preferences.clusterHealthProperty().getValue().autoUpdateIntervall);
    timer.startedProperty().bind(Preferences.clusterHealthProperty().getValue().autoUpdateEnabled);
  }
  
  private PopOver initPopoverButton(Button button, String fxmlLocation) {
    Node limits = null;
    try {
      final FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource(fxmlLocation));
      loader.setResources(resources);
      limits = loader.load();
    } catch (Exception e) {
      LOGGER.error("Could not load cluster health pane", e);
      ((Pane) button.getParent()).getChildren().remove(button);
      return null;
    }
    
    final PopOver result = new PopOver(limits);
    result.setHideOnEscape(true);
    result.setAutoHide(true);
    result.setDetachable(false);
    result.setArrowLocation(ArrowLocation.TOP_RIGHT);
    return result;
  }
  
  private void togglePopOver(PopOver p, Node parent) {
    if (p == null) {
      return;
    }
    if (p.isShowing()) {
      p.hide();
    } else {
      p.show(parent);
    }
  }
  
  @FXML
  protected void showDefineWarningsPopover() {
    togglePopOver(defineWarningsPopover, defineWarnings);
  }
  
  @FXML
  protected void showAddInstancePopover() {
    togglePopOver(addInstancePopover, addInstance);
  }

  
  @FXML
  private void runUpdate() {
    controlElements.forEach(ce -> runUpdateAsyn(ce));
  }
  
  private void runUpdateAsyn(StatusControl control) {
    exec.submit(() -> {
      LOGGER.info("Running update for " + control.getClusterInstance().serverNameProperty().get());
      control.getClusterInstance().update();
      }
    );
  }
  
  protected void removeClusterInstance(StatusControl controlElement) {
    settings.instances.remove(controlElements.indexOf(controlElement));
  }
  

  private void loadControlElements() {
    settings.instances.forEach(instance -> {
      controlElements.add(new StatusControl(new ClusterInstance(instance), this));
    });
    settings.instances.addListener((ListChangeListener<? super String>) change -> {
      while (change.next()) {
        change.getAddedSubList().forEach(added -> {
          final StatusControl newInstance = new StatusControl(new ClusterInstance(added), this);
          controlElements.add(newInstance);
          runUpdateAsyn(newInstance);
        });
        if (change.wasRemoved()) {
          controlElements.remove(change.getFrom());
        }
        // TODO: Support for permutation / update
      }
      // Workaround against GridView issue #494
      // https://bitbucket.org/controlsfx/controlsfx/issue/494/gridview-not-correctly-reflecting-changes
      final double tmp = gridView.getHorizontalCellSpacing();
      gridView.setHorizontalCellSpacing(-99);
      gridView.setHorizontalCellSpacing(tmp);
    });
  }
}

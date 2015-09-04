package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.io.IOException;
import java.time.Duration;
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
import org.levigo.jadice.server.converterclient.util.FxAnimationScheduler;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

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
  
  private final Duration updateRate = Duration.ofSeconds(60);
  
  private FxAnimationScheduler timer;
  
  private long nextUpdate = -1;
  
  private PopOver defineWarningsPopover;

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    
    loadControlElements();
    initWarningsRulesButton();
    
    hiddenSidePane.pinnedSideProperty().bind(new When(toggleSettingsButton.selectedProperty()).then(Side.TOP).otherwise((Side) null));
    
    gridView.setCellFactory(view -> new StatusControlGridCell());
    gridView.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    gridView.setItems(controlElements);
    timer = new FxAnimationScheduler(() -> {
        long now = System.nanoTime();
        if (nextUpdate == -1) {
          nextUpdate = now + TimeUnit.SECONDS.toNanos(5);
        }
        if (now >= nextUpdate) {
          runUpdate();
        }
      });
    timer.startedProperty().bind(Preferences.clusterHealthProperty().getValue().autoUpdateEnabled);
  }
  
  private void initWarningsRulesButton() {
    Node limits = null;
    try {
      final FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/fxml/ConfigureClusterHealthWarnings.fxml"));
      loader.setResources(resources);
      limits = loader.load();
    } catch (IOException e) {
      LOGGER.error("Could not load cluster health pane", e);
      ((FlowPane) defineWarnings.getParent()).getChildren().remove(defineWarnings);
      return;
    }
    
    defineWarningsPopover = new PopOver(limits);
    defineWarningsPopover.setHideOnEscape(true);
    defineWarningsPopover.setAutoHide(true);
    defineWarningsPopover.setDetachable(false);
    defineWarningsPopover.setArrowLocation(ArrowLocation.TOP_RIGHT);
  }
  
  @FXML
  protected void showDefineWarningsPopover() {
    if (defineWarningsPopover == null) {
      return;
    }
    if (defineWarningsPopover.isShowing()) {
      defineWarningsPopover.hide();
    } else {
      defineWarningsPopover.show(defineWarnings);
    }
  }

  
  @FXML
  private void runUpdate() {
    controlElements.forEach(ce -> runUpdateAsyn(ce));
    nextUpdate = System.nanoTime() + updateRate.toNanos();
  }
  
  private void runUpdateAsyn(StatusControl control) {
    exec.submit(() -> {
      LOGGER.info("Running update for " + control.getClusterInstance().serverNameProperty().get());
      control.getClusterInstance().update();
      }
    );
  }
  
  @FXML
  private void onAddInstance() {
    final TextInputDialog inputDialog = new TextInputDialog();
    inputDialog.setTitle(resources.getString("cluster-health.add-instance.title"));
    inputDialog.setHeaderText(resources.getString("cluster-health.add-instance.header-text"));
    inputDialog.initModality(Modality.WINDOW_MODAL);
    inputDialog.initOwner(addInstance.getScene().getWindow());
    
    inputDialog.showAndWait().ifPresent(jmxUrl -> {
      settings.instances.add(jmxUrl);
      });
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

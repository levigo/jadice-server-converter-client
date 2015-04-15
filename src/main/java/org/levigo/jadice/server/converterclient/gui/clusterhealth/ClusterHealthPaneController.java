package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.time.Duration;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import org.controlsfx.control.GridView;
import org.controlsfx.control.HiddenSidesPane;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentAverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentEfficiencyRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ServerRunningRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule;
import org.levigo.jadice.server.converterclient.util.UiUtil;


public class ClusterHealthPaneController {

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

  private final ObservableList<Rule<?>> rules = FXCollections.observableArrayList();

  private final ObservableList<StatusControl> controlElements = FXCollections.observableArrayList();
  
  private final Duration updateRate = Duration.ofSeconds(60);
  
  private AnimationTimer timer;
  
  private long nextUpdate = -1;

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    loadRules();
    loadControlElements();
    
    hiddenSidePane.pinnedSideProperty().bind(new When(toggleSettingsButton.selectedProperty()).then(Side.TOP).otherwise((Side) null));
    
    gridView.setCellFactory(view -> new StatusControlGridCell());
    gridView.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    gridView.setItems(controlElements);
    timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (nextUpdate == -1) {
          nextUpdate = now + TimeUnit.SECONDS.toNanos(5);
        }
        if (now >= nextUpdate) {
          runUpdate();
        }
      }
    };
    timer.start();
  }
  
  @FXML
  private void runUpdate() {
    controlElements.forEach(ce -> runUpdateAsyn(ce));
    nextUpdate = System.nanoTime() + updateRate.toNanos();
  }
  
  private void runUpdateAsyn(StatusControl control) {
    exec.submit(() -> {
      System.out.println("Running update for " + control.getClusterInstance().serverNameProperty().get());
      control.getClusterInstance().update();
      }
    );
  }
  
  private void loadRules() {
    // TODO: make them editable
  }
  
  @FXML
  private void onAddInstance() {
    final TextInputDialog inputDialog = new TextInputDialog();
    inputDialog.setTitle(resources.getString("cluster-health.add-instance.title"));
    inputDialog.setHeaderText(resources.getString("cluster-health.add-instance.header-text"));
    inputDialog.initModality(Modality.WINDOW_MODAL);
    inputDialog.initOwner(addInstance.getScene().getWindow());
    
    inputDialog.showAndWait().ifPresent(jmxUrl -> {
      final StatusControl newInstance = new StatusControl(new ClusterInstance(jmxUrl, rules));
      controlElements.add(newInstance);
      runUpdateAsyn(newInstance);
      });
  }
  

  private void loadControlElements() {
    // TODO: make them persistent
  }

}

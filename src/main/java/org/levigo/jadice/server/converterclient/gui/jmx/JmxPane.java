package org.levigo.jadice.server.converterclient.gui.jmx;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.jmx.JMXHandler.CallbackHandler;
import org.levigo.jadice.server.converterclient.gui.jmx.JMXHandler.ConnectionStatus;
import org.levigo.jadice.server.converterclient.util.UiUtil;


public class JmxPane extends BorderPane {
  
  private class MyCallbackHandler implements CallbackHandler {

    @Override
    public void jobStateEventOccured(JobStateEventDTO jobStateEvent) {
      Platform.runLater(() -> {
        performance.addObservation(jobStateEvent);
        durationDistribution.addObservation(jobStateEvent);
        gauges.addObservation(jobStateEvent);
      });
    }

    @Override
    public void updatePerformanceInfo(PerformanceInfoDTO performanceInfo) {
      Platform.runLater(() -> {
        performance.updatePerformanceInfo(performanceInfo);
        durationDistribution.updatePerformanceInfo(performanceInfo);
        gauges.updatePerformanceInfo(performanceInfo);
      });
    }
    
    @Override
    public void connectionEstablished(String serverVersion) {
      Platform.runLater(() -> 
        gauges.connectionEstablished(serverVersion)
      );
      
    }

    @Override
    public void connectionFailed(Throwable reason) {
      Platform.runLater(() -> {
        gauges.connectionFailed();
        Dialogs.create()
          .style(DialogStyle.NATIVE)
          .title("Error")
          .message("Could not connect to JMX")
          .showException(reason);
      });
    }

    @Override
    public void connectionClosed() {
      Platform.runLater(() ->
        // set all values to null
        gauges.updatePerformanceInfo(new PerformanceInfoDTO())
      );
    }

  }
    
  @FXML
  private Button home;
  
  @FXML
  private ComboBox<String> servers;
  
  @FXML
  private Button connect;
  
  @FXML
  private Button clearView;
  
  @FXML
  private PerformanceChart performance;
  
  @FXML
  private DurationDistributionChart durationDistribution;
  
  @FXML
  private GaugesRatesPane gauges;
  
  private final JMXHandler jmxHandler = new JMXHandler(new MyCallbackHandler());
  

  public JmxPane() {
    
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JmxPane.fxml"));

    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    UiUtil.configureHomeButton(home);
    initConnectionPane();

    forceEagerRendering();
  }

  private void forceEagerRendering() {
    // The JMX pane needs some time to init when rendered first,
    // so we force this eagerly
    final Scene dummy = new Scene(this);
    dummy.snapshot(null);
    dummy.setRoot(new Group());
  }

  private void initConnectionPane() {
    servers.itemsProperty().bind(Preferences.recentJmxUrlsProperty());
    servers.setValue(servers.getItems().get(0));
    
    connect.setOnAction(event -> {
      ConnectionStatus status = jmxHandler.getConnectionStatusProperty().get();
      switch (status) {
        case DISCONNECTED:
          final String url = servers.getValue();
          jmxHandler.openConnection(url);
          break;

        case CONNECTED:
          jmxHandler.closeConnection();
          break;

        case CONNECTING:
          // Do nothing
          break;
          
        default:
          throw new IllegalArgumentException("Unsuported status " + status);
      }
    });
    
    clearView.setOnAction(event -> {
      performance.clear();
      durationDistribution.clear();
      gauges.clear();
    });
    
    jmxHandler.getConnectionStatusProperty().addListener(event -> {
      ConnectionStatus status = jmxHandler.getConnectionStatusProperty().get();
      Platform.runLater(() -> {
        switch (status) {
          case CONNECTING:
            connect.setDisable(true);
            connect.setText("Connecting...");
            break;
            
          case CONNECTED:
            connect.setDisable(false);
            connect.setText("Disconnect");
            break;
  
          case DISCONNECTED:
            connect.setDisable(false);
            connect.setText("Connect");
            break;

          default:
            throw new IllegalArgumentException("Unsuported status " + status);
        }
      });
    });
  }

}

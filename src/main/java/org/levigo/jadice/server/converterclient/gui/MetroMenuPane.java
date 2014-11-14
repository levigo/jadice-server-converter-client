package org.levigo.jadice.server.converterclient.gui;

import java.io.IOException;

import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.Preferences.UpdatePolicy;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateCheckResult;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateDialogs;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateService;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;


public class MetroMenuPane extends BorderPane {
  
  @FXML
  Button conversion;
  
  @FXML
  Button serverLog;
  
  @FXML
  Button jmx;
  
  @FXML
  Button inspector;
  
  @FXML
  Button options;
  
  @FXML
  Button about;
  
  @FXML
  Button fullscreen;
  
  @FXML
  Button exitFullscreen;
  
  @FXML
  Pane bottomBar;
  
  @FXML
  Button update;
  
  
  private static final String ICON_SIZE = "160px";
  
  private static final String ICON_SIZE_SMALL = "20px";
  
  public MetroMenuPane() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MetroMenuPane.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    initIconButtons();
  }
  
  protected void hideBottomBar() {
    final TranslateTransition translateOut = new TranslateTransition(Duration.millis(80), bottomBar);
    // Move buttons out at the bottom of the window
    translateOut.setFromY(0);
    translateOut.setToY(bottomBar.getHeight());
    
    // Caveat: the whole panel is moving concurrently from right to left, so animate in opposite direction
    translateOut.setFromX(0);
    translateOut.setToX(getScene().getWidth() / 2);
    translateOut.play();
  }
  
  protected void showBottomBar() {
    final TranslateTransition translateIn = new TranslateTransition(Duration.millis(80), bottomBar);
    translateIn.setToY(0);
    translateIn.setFromY(bottomBar.getHeight());

    // See comment in hideBottomBar!
    translateIn.setFromX(getScene().getWidth() / 2);
    translateIn.setToX(0);
    
    translateIn.play();
  }
  
  private void initIconButtons() {
    initIconButton(conversion, AwesomeIcon.GEARS, evt -> ConverterClientApplication.getInstance().openConversion());
    initIconButton(serverLog, AwesomeIcon.TASKS, evt -> ConverterClientApplication.getInstance().openServerLog());
    initIconButton(jmx, AwesomeIcon.AREA_CHART, evt -> ConverterClientApplication.getInstance().openJMX());
    initIconButton(inspector, AwesomeIcon.SEARCH, evt -> ConverterClientApplication.getInstance().openInspector());
    initSmallIconButton(options, AwesomeIcon.SLIDERS, evt -> ConverterClientApplication.getInstance().openOptions());
    initSmallIconButton(about, AwesomeIcon.INFO, evt -> ConverterClientApplication.getInstance().openAbout());
    initSmallIconButton(fullscreen, AwesomeIcon.EXPAND, evt -> {((Stage)getScene().getWindow()).setFullScreen(true);});
    initSmallIconButton(exitFullscreen, AwesomeIcon.COMPRESS, evt -> {((Stage)getScene().getWindow()).setFullScreen(false);});
    initSmallIconButton(update, AwesomeIcon.BULLHORN, evt -> {
      final UpdateCheckResult result = UpdateService.getInstance().getValue();
      if (result != null && result.isNewerVersionAvailable()) {
        UpdateDialogs.showUpdateAvailableDialog(result);
      } else {
        // Should not happen!
        UpdateDialogs.showNoUpdateAvailableDialog();
      }
    });
    
    bindVisibility();
  }

  private void bindVisibility() {
    Platform.runLater(() -> {
      if (getScene() == null || getScene().getWindow() == null) {
        // Re-try it later
        bindVisibility();
        return;
      }
      final ReadOnlyBooleanProperty fullScreenProperty = ((Stage) getScene().getWindow()).fullScreenProperty();
      
      fullscreen.visibleProperty().bind(fullScreenProperty.not());
      fullscreen.managedProperty().bind(fullScreenProperty.not());
      exitFullscreen.visibleProperty().bind(fullScreenProperty);
      exitFullscreen.managedProperty().bind(fullScreenProperty);
    });

    UpdateService.getInstance().addEventFilter(Event.ANY, evt -> {
      final UpdateCheckResult result = UpdateService.getInstance().getValue();
      update.setVisible(result != null && result.isNewerVersionAvailable());
    });
    if (UpdatePolicy.ON_EVERY_START.equals(Preferences.updatePolicyProperty().getValue())) {
      Platform.runLater(() -> {
        if (Worker.State.READY.equals(UpdateService.getInstance().getState())) {
          UpdateService.getInstance().start();
        }
      });
    }
  }
  
  private void initSmallIconButton(Button button, AwesomeIcon icon, EventHandler<ActionEvent> evt) {
    final Tooltip tooltip = new Tooltip();
    tooltip.textProperty().bind(button.textProperty());
    button.setTooltip(tooltip);

    AwesomeDude.setIcon(button, icon, ICON_SIZE_SMALL, ContentDisplay.GRAPHIC_ONLY);
    button.setOnAction(evt);
  }
  
  private void initIconButton(Button button, AwesomeIcon icon, EventHandler<ActionEvent> evt) {
    AwesomeDude.setIcon(button, icon, ICON_SIZE, ContentDisplay.TOP);
    button.setOnAction(evt);
  }
}

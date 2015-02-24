package org.levigo.jadice.server.converterclient.gui;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;

import java.io.IOException;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.levigo.jadice.server.converterclient.JobCard;
import org.levigo.jadice.server.converterclient.JobCardScheduler;
import org.levigo.jadice.server.converterclient.gui.inspector.ConfigurationInspectorPaneController;

import com.levigo.jadice.server.client.JobFactory;

public class ConverterClientApplication extends Application {
  
  private final static String APPLICATION_TITLE = getUiResources().getString("app.title");
  
  private final static String API_VERSION = JobFactory.class.getPackage() == null
      ? null
      : JobFactory.class.getPackage().getImplementationVersion();
  
  
  private final Pane menu;
  
  private final MetroMenuPaneController menuController;
  
  private final Pane conversionPane;
  
  private final Pane serverlogPane;

  private final Pane jmxPane;
  
  private final Pane inspectorPane;
  
  private final ConfigurationInspectorPaneController inspectorPaneController;
  
  private final Pane optionsPane;
  
  private final Pane aboutPane;
  
  private Scene scene;
  
  private static ConverterClientApplication instance;

  public static ConverterClientApplication getInstance() {
    return instance;
  }
  
  public ConverterClientApplication() throws IOException {
    instance = this;
    
    aboutPane = FXMLLoader.load(getClass().getResource("/fxml/AboutPane.fxml"), getUiResources());
    conversionPane = FXMLLoader.load(getClass().getResource("/fxml/ConversionPane.fxml"), getUiResources());
    jmxPane = FXMLLoader.load(getClass().getResource("/fxml/JmxPane.fxml"), getUiResources());
    serverlogPane = FXMLLoader.load(getClass().getResource("/fxml/LogPane.fxml"), getUiResources());
    optionsPane = FXMLLoader.load(getClass().getResource("/fxml/OptionsPane.fxml"), getUiResources());

    final FXMLLoader menuLoader = new FXMLLoader();
    menuLoader.setResources(getUiResources());
    menuLoader.setLocation(getClass().getResource("/fxml/MetroMenuPane.fxml"));
    menu = menuLoader.load();
    menuController = menuLoader.getController();

    final FXMLLoader inspectorLoader = new FXMLLoader();
    inspectorLoader.setResources(getUiResources());
    inspectorLoader.setLocation(getClass().getResource("/fxml/ConfigurationInspectionPane.fxml"));
    inspectorPane = inspectorLoader.load();
    inspectorPaneController = inspectorLoader.getController();
  }

  @Override
  public void start(Stage stage) {
    scene = new Scene(menu);
    final String css = getClass().getResource("/css/MetroMenu.css").toExternalForm();
    scene.getStylesheets().add(css);

    stage.setMinHeight(700);
    stage.setMinWidth(920);
    
    stage.setScene(scene);
    stage.show();
    stage.setTitle(String.format(APPLICATION_TITLE, API_VERSION == null ? "" : API_VERSION));
    stage.getIcons().addAll(Icons.getAllIcons());
    
    stage.setOnCloseRequest(event -> {
      JobCardScheduler.getInstance().shutdown();
    });
    
    // XXX: Workaround: SwingNode prevents application from stopping automatically
    stage.setOnHidden(event -> {
      System.exit(0);
    });

    scene.setFill(lookupDefaultBgColor());
    enableFullScreen(stage);
  }

  private Paint lookupDefaultBgColor() {
    final List<BackgroundFill> fills = menu.getBackground().getFills();
    if (fills == null || fills.isEmpty()) {
      return null;
    }
    return fills.get(0).getFill();
  }

  private void enableFullScreen(Stage stage) {
    stage.setFullScreenExitHint("");
    scene.setOnKeyPressed(evt -> {
      switch (evt.getCode()) {
        case F11 :
          stage.setFullScreen(!stage.isFullScreen());
          evt.consume();
          break;
          
        default:
         // do nothing
      }
    });
  }

  public void openInspector(JobCard jobCard) {
    displayPanel(inspectorPane);
    inspectorPaneController.showGraph(jobCard);
  }
  
  public void openMenu() {
    displayPanel(menu);
  }
  
  public void openConversion() {
    displayPanel(conversionPane);
  }
  
  public void openJMX() {
    displayPanel(jmxPane);
  }
  
  public void openServerLog() {
    displayPanel(serverlogPane);
  }
  
  public void openInspector() {
    displayPanel(inspectorPane);
  }
  
  public void openOptions() {
    displayPanel(optionsPane);
  }
  
  public void openAbout() {
    displayPanel(aboutPane);
  }
  

  private void displayPanel(Pane newPanel) {
    final Parent oldPanel = scene.getRoot();
    if (oldPanel == newPanel) {
      return;
    }
    
    
    final FadeTransition fadeOut = new FadeTransition(Duration.millis(50), oldPanel);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    final TranslateTransition translateOut = new TranslateTransition(Duration.millis(80), oldPanel);
    translateOut.setFromX(0);
    translateOut.setToX(-scene.getWidth()/2);
    
    final ParallelTransition parallelOut = new ParallelTransition(fadeOut, translateOut);
    parallelOut.play();
    if (oldPanel == menu) {
      menuController.hideBottomBar();
    }

    parallelOut.setOnFinished(evt -> {
      scene.setRoot(newPanel);
      
      final FadeTransition fadeIn = new FadeTransition(Duration.millis(50), newPanel);
      fadeIn.setFromValue(0.0);
      fadeIn.setToValue(1.0);
      final TranslateTransition translateIn = new TranslateTransition(Duration.millis(80), newPanel);
      translateIn.setFromX(-scene.getWidth()/2);
      translateIn.setToX(0);
      final ParallelTransition parallelIn = new ParallelTransition(fadeIn, translateIn);
      parallelIn.play();
      if (newPanel == menu) {
        menuController.showBottomBar();
      }
    });
    
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}
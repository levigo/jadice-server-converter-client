package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import eu.hansolo.enzo.canvasled.Led;
import eu.hansolo.enzo.canvasled.LedBuilder;

public class StatusControl extends AnchorPane {

  private final Led green = buildLed(Color.LAWNGREEN);
  
  private final Led yellow = buildLed(Color.GOLD);
  
  private final Led red = buildLed(Color.RED);
  
  private final Tooltip messagesTooltip = new Tooltip();
  
  private final Text instanceName = new Text();
  
  private final ClusterInstance instance;
  
  private final ClusterHealthPaneController controller;
  
  private final HBox middleBox = new HBox();
  
  private final HBox innerBox = new HBox();
  
  private final Button removeBttn = new Button("x");
  
  public StatusControl(ClusterInstance instance, ClusterHealthPaneController controller) {
    this.instance = instance;
    this.controller = controller;
    middleBox.getChildren().add(innerBox);
    innerBox.getChildren().add(red);
    innerBox.getChildren().add(yellow);
    innerBox.getChildren().add(green);
    innerBox.setMinWidth(USE_PREF_SIZE);
    final LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.BLACK), new Stop(1, Color.DIMGRAY));
    innerBox.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(15), null)));
    innerBox.setPadding(new Insets(2, 5, 2, 5));
    middleBox.getChildren().add(instanceName);
    middleBox.setMaxHeight(USE_PREF_SIZE);
    HBox.setMargin(innerBox, new Insets(2, 0, 2, 3));
    HBox.setMargin(instanceName, new Insets(0, 5, 0, 10));
    middleBox.setAlignment(Pos.CENTER_LEFT);
    
    middleBox.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(9), new Insets(-2))));
    getChildren().add(middleBox);
    AnchorPane.setLeftAnchor(middleBox, 0.0);
    AnchorPane.setRightAnchor(middleBox, 0.0);
    AnchorPane.setTopAnchor(middleBox, 0.0);
    AnchorPane.setBottomAnchor(middleBox, 0.0);
    
    getChildren().add(removeBttn);
    AnchorPane.setRightAnchor(removeBttn, 0.0);
    
    instanceName.setStyle("-fx-font-weight: bold;");
    initBindings();
    
    configureRemoveButton();
  }

  private void configureRemoveButton() {
    removeBttn.setVisible(false);
    setOnMouseEntered(evt -> removeBttn.setVisible(true));
    setOnMouseExited(evt -> removeBttn.setVisible(false));
    removeBttn.setOnAction(evt -> controller.removeClusterInstance(this));
  }
  
  public ClusterInstance getClusterInstance() {
    return instance;
  }
  
  private void initBindings() {
    green.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.GOOD));
    yellow.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.ATTENTION));
    red.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.FAILURE));
    instanceName.textProperty().bind(instance.serverNameProperty());
    instance.messagesProperty().addListener((ListChangeListener.Change<? extends String>  c) -> {
      if (instance.messagesProperty().get().isEmpty()) {
        Tooltip.uninstall(innerBox, messagesTooltip);
      } else {
        Tooltip.install(innerBox, messagesTooltip);
        messagesTooltip.setText(instance.messagesProperty().stream().collect(Collectors.joining("\n")));
      }
    });
  }

  private static Led buildLed(Color color) {
    return LedBuilder.create()//
    .ledColor(color)//
    .frameVisible(false)//
    .minSize(30, 30)//
    .build();
  }


}

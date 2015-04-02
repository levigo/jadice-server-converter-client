package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import eu.hansolo.enzo.led.Led;
import eu.hansolo.enzo.led.LedBuilder;

public class StatusControl extends HBox {

  private final Led green = buildLed(Color.LAWNGREEN);
  
  private final Led yellow = buildLed(Color.GOLD);
  
  private final Led red = buildLed(Color.RED);
  
  private final Text instanceName = new Text();
  
  private final ClusterInstance instance;
  
  private final HBox innerBox = new HBox();
  
  public StatusControl(ClusterInstance instance) {
    this.instance = instance;
    getChildren().add(innerBox);
    innerBox.getChildren().add(red);
    innerBox.getChildren().add(yellow);
    innerBox.getChildren().add(green);
    innerBox.setMinWidth(USE_PREF_SIZE);
    final LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.BLACK), new Stop(1, Color.DIMGRAY));
    innerBox.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(15), null)));
    innerBox.setPadding(new Insets(2, 5, 2, 5));
    getChildren().add(instanceName);
    setMaxHeight(USE_PREF_SIZE);
    setMargin(innerBox, new Insets(0, 0, 0, 3));
    setMargin(instanceName, new Insets(0, 5, 0, 10));
    setAlignment(Pos.CENTER_LEFT);
    
    setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, new CornerRadii(9), new Insets(-2))));
    
    instanceName.setStyle("-fx-font-weight: bold;");
    initBindings();
  }
  
  private void initBindings() {
    green.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.GOOD));
    yellow.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.ATTENTION));
    red.onProperty().bind(instance.healthProperty().isEqualTo(HealthStatus.FAILURE));
    instanceName.textProperty().bind(instance.serverNameProperty());
  }

  private static Led buildLed(Color color) {
    return LedBuilder.create()//
    .ledColor(color)//
    .frameVisible(false)//
    .minSize(30, 30)//
    .build();
  }


}

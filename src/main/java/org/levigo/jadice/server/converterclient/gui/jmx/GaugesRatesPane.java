package org.levigo.jadice.server.converterclient.gui.jmx;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import eu.hansolo.enzo.common.Section;
import eu.hansolo.enzo.gauge.Gauge;
import eu.hansolo.enzo.gauge.GaugeBuilder;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;


public class GaugesRatesPane extends VBox implements Chart {
  
  private final Lcd totalJobCount;
  private final Gauge currentJobCount;
  private final Gauge abortRate;
  private final Gauge failureRate;

  
  public GaugesRatesPane() {
    super(5);
    setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    setPadding(new Insets(5));
    setMinSize(150, 400);

    totalJobCount = createTotalJobCountLcd();
    currentJobCount = createJobCountGauge();
    abortRate = createAbortRateGauge();
    failureRate = createFailureRateGauge();
    
    getChildren().addAll(totalJobCount, currentJobCount, abortRate, failureRate);
  }
  
  

  private Lcd createTotalJobCountLcd() {
    Lcd lcd = LcdBuilder.create()
        .minHeight(60)
        .maxHeight(60)
        .prefHeight(60)
        .minWidth(150)
        .maxWidth(150)
        .prefWidth(150)
        .styleClass(Lcd.STYLE_CLASS_WHITE)
        .noFrame(false)
        .foregroundShadowVisible(false)
        .maxValue(Double.POSITIVE_INFINITY)
        .crystalOverlayVisible(false)
        .title("Total Job Count")
        .batteryVisible(false)
        .signalVisible(false)
        .alarmVisible(false)
        // Threshold makes it possible to display "-E-" and a WARN sign when connection is lost
        .threshold(1000000000)
        .thresholdVisible(true)
        .decimals(0)
        .animationDurationInMs(1500)
        .lowerRightTextVisible(false)
        .valueFont(Lcd.LcdFont.LCD)
        .animated(true)
        .build();
    return lcd;
  }

  private Gauge createJobCountGauge() {
    final Gauge g = GaugeBuilder.create()
        .prefSize(150, 150)
        .animated(true)
        .autoScale(false)
        .startAngle(330)
        .angleRange(300)
        .minValue(0)
        .maxValue(10)
        .sectionsVisible(true)
        .sections(new Section(0, 4),
                  new Section(4, 8),
                  new Section(8,10))
        .areas(new Section(8, 10))
        .areasVisible(true)
        .majorTickSpace(2)
        .minorTickSpace(1)
        .decimals(0)
        .plainValue(false)
        .tickLabelOrientation(Gauge.TickLabelOrientation.HORIZONTAL)
        .title("Current Jobs")
        .unit("#")
        .build();
    g.setSectionFill0(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0.5, Color.LIMEGREEN) , new Stop(0.0, Color.ORANGE)));
    g.setSectionFill1(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0.8, Color.ORANGE) , new Stop(1.0, Color.RED)));
    g.setSectionFill2(Color.RED);
    g.setAreaFill0(Color.RED.deriveColor(1.0, 1.0, 1.0, 0.3));
    g.setMouseTransparent(true);
    return g;
  }

  private Gauge createAbortRateGauge() {
    return createRateGauge("abort rate");
  }

  private Gauge createFailureRateGauge() {
    return createRateGauge("failure rate");
  }
  
  private Gauge createRateGauge(String title) {
    final Gauge g = GaugeBuilder.create()
        .prefSize(150, 150)
        .animated(true)
        .startAngle(330)
        .angleRange(300)
        .minValue(0)
        .maxValue(5)
        .sectionsVisible(true)
        .sections(new Section(0, 2),
                  new Section(2, 4),
                  new Section(4, 5))
        .areas(new Section(4, 5))
        .majorTickSpace(1)
        .minorTickSpace(0.5d)
        .decimals(2)
        .plainValue(false)
        .autoScale(false)
        .tickLabelOrientation(Gauge.TickLabelOrientation.HORIZONTAL)
        .title(title)
        .unit("%")
        .build();
    g.setSectionFill0(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0.5, Color.LIMEGREEN) , new Stop(0.0, Color.ORANGE)));
    g.setSectionFill1(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0.8, Color.ORANGE) , new Stop(1.0, Color.RED)));
    g.setSectionFill2(Color.RED);
    g.setAreaFill0(Color.RED.deriveColor(1.0, 1.0, 1.0, 0.3));
    g.interactiveProperty().addListener(event -> {
      if (g.isInteractive()) {
        g.setInteractive(false);
      }
    });
    return g;
    
  }

  @Override
  public void addObservation(JobStateEventDTO event) {
    // Don't care
  }



  @Override
  public void updatePerformanceInfo(PerformanceInfoDTO p) {
    totalJobCount.setValue(p.completedTaskCount);
    currentJobCount.setValue(p.activeJobCount);
    abortRate.setValue(p.abortRate * 100.0); // Absolute to Percentage
    failureRate.setValue(p.failureRate * 100.0); // Absolute to Percentage
    
    if (currentJobCount.getMaxValue() != p.maxJobCount) {
      // FIXME: handle max job count!
    }
  }



  @Override
  public void clear() {
    totalJobCount.setValue(0);
    currentJobCount.setValue(0);
    abortRate.setValue(0);
    failureRate.setValue(0);
  }



  public void connectionEstablished(String serverVersion) {
    totalJobCount.setLowerRightTextVisible(true);
    if (serverVersion.length() > 12) {
      serverVersion = serverVersion.substring(0, 12);
    }
    totalJobCount.setLowerRightText("v." + serverVersion);
  }



  public void connectionFailed() {
    totalJobCount.setLowerRightTextVisible(false);
    totalJobCount.setValue(Double.MAX_VALUE);
    currentJobCount.setValue(0.0);
    abortRate.setValue(0.0);
    failureRate.setValue(0.0);
  }


  
}

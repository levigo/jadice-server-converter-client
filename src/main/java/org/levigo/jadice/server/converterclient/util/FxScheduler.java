package org.levigo.jadice.server.converterclient.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * An enhancement of the java FX default {@link AnimationTimer} which can be
 * configured via properties
 */
public class FxScheduler {
  
  private static final Logger LOGGER = Logger.getLogger(FxScheduler.class);
  
  private final BooleanProperty startedProperty = new SimpleBooleanProperty();
  
  private long lastExecution = -1;
  
  private long nextExecution = -1;
  
  private LongProperty executionRateProperty = new SimpleLongProperty();
  
  private ObjectProperty<TimeUnit> executionUnitProperty = new SimpleObjectProperty<>(TimeUnit.SECONDS);
  
  // FIXME: This is a misuse of the FX thread... this timer will be invoked every few milliseconds!
  private final AnimationTimer timer;
  
  public FxScheduler(Runnable handler) {
    Objects.requireNonNull(handler, "handler");
    
    this.timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (now < nextExecution) {
          return;
        }
        handler.run();
        lastExecution = now;
        nextExecution = calculateNextExecution();
      }
    };
    startedProperty.addListener(this::startedChanged);
    executionRateProperty.addListener(this::updateRateChanged);
    executionUnitProperty.addListener(this::updateRateChanged);
    
  }
  
  private void startedChanged(Observable change) {
    if (isStarted()) {
      LOGGER.debug("Starting scheduler");
      timer.start();
    } else {
      LOGGER.debug("Stopping scheduler");
      timer.stop();
    }
  }
  
  private void updateRateChanged(Observable change) {
    LOGGER.info(String.format("Changed update rate to %d %s", executionRateProperty.get(),  executionUnitProperty.get()));
    nextExecution = calculateNextExecution();
  }
  
  private long calculateNextExecution() {
    if (lastExecution < 0) {
      return -1;
    }
    return lastExecution + executionUnitProperty.get().toNanos(executionRateProperty.get());
  }

  public BooleanProperty startedProperty() {
    return startedProperty;
  }
  
  public void setStarted(boolean started) {
    startedProperty.set(started);
  }
  
  public boolean isStarted() {
    return startedProperty.get();
  }
  
  public LongProperty executionRateProperty() {
    return executionRateProperty;
  }
  
  public void setExecutionRate(long value) {
    if (value <= 0) {
      throw new IllegalArgumentException("value must be greater zero");
    }
    executionRateProperty.set(value);
  }
  
  public long getExecutionRate() {
    return executionRateProperty.get();
  }
  
  public ObjectProperty<TimeUnit> executionUnitProperty() {
    return executionUnitProperty;
  }
  
  public void setExecutionUnit(TimeUnit unit) {
    executionUnitProperty.set(Objects.requireNonNull(unit, "unit"));
  }
  
  public TimeUnit getExecutionUnit() {
    return executionUnitProperty.get();
  }

}

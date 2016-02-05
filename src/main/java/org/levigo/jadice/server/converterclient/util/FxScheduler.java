package org.levigo.jadice.server.converterclient.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A simple repeating timer which can be paused
 */
public class FxScheduler {
  
  private static final Logger LOGGER = Logger.getLogger(FxScheduler.class);
  
  private static final Duration INTERVAL = Duration.of(5, ChronoUnit.SECONDS);
  
  private final BooleanProperty startedProperty = new SimpleBooleanProperty();
  
  private long lastExecution = -1;
  
  private long nextExecution = -1;
  
  private LongProperty executionRateProperty = new SimpleLongProperty();
  
  private ObjectProperty<TimeUnit> executionUnitProperty = new SimpleObjectProperty<>(TimeUnit.SECONDS);
  
  private final Timer timer = new Timer("FX-Scheduler", true);
  
  private TimerTask schedulerTask;
  
  public FxScheduler(Runnable handler) {
    Objects.requireNonNull(handler, "handler must not be null");
    schedulerTask = createTask(handler);
    timer.schedule(schedulerTask, INTERVAL.toMillis(), INTERVAL.toMillis());
    
    executionRateProperty.addListener(this::updateRateChanged);
    executionUnitProperty.addListener(this::updateRateChanged);
    
  }
  
  private TimerTask createTask(Runnable handler) {
    return new TimerTask() {
      
      @Override
      public void run() {
        final long now = System.currentTimeMillis();
        if (!isStarted() || now < nextExecution) {
          return;
        }
        handler.run();
        lastExecution = now;
        nextExecution = calculateNextExecution();
      }
      
    };
  }
  
  private void updateRateChanged(Observable change) {
    LOGGER.info(String.format("Changed update rate to %d %s", executionRateProperty.get(),  executionUnitProperty.get()));
    nextExecution = calculateNextExecution();
  }
  
  private long calculateNextExecution() {
    if (lastExecution < 0) {
      return -1;
    }
    return lastExecution + executionUnitProperty.get().toMillis(executionRateProperty.get());
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

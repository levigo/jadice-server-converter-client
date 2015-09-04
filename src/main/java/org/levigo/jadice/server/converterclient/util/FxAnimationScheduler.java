package org.levigo.jadice.server.converterclient.util;

import java.util.Objects;

import org.apache.log4j.Logger;

import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * An enhancement of the java FX default {@link AnimationTimer}
 */
public class FxAnimationScheduler {
  
  private static final Logger LOGGER = Logger.getLogger(FxAnimationScheduler.class);
  
  @FunctionalInterface
  public static interface Handler {
    void handle();
  }
  
  private final BooleanProperty startedProperty = new SimpleBooleanProperty();
  
  private final AnimationTimer timer;
  
  public FxAnimationScheduler(Handler handler) {
    Objects.requireNonNull(handler, "handler");
    
    this.timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        handler.handle();
      }
    };
    startedProperty.addListener(this::changeRunning);
  }
  
  private void changeRunning(Observable change) {
    if (isStarted()) {
      LOGGER.info("Starting scheduler");
      timer.start();
    } else {
      LOGGER.info("Stopping scheduler");
      timer.stop();
    }
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

}

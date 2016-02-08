package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Optional;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ServerRunningRule implements ImmutableBooleanRule {
  
  public static final ServerRunningRule INSTANCE = new ServerRunningRule(true);
  
  private final BooleanProperty enabledProperty;
  
  public ServerRunningRule(boolean isEnabled) {
    enabledProperty = new SimpleBooleanProperty(isEnabled);
  }
  
  @Override
  public String getDescription() {
    return "Instance is running";
  }
  
  @Override
  public boolean getExpectedValue() {
    return true;
  }
  
  @Override
  public BooleanProperty enabledProperty() {
    return enabledProperty;
  }

  @Override
  public EvaluationResult<Boolean> evaluate(MBeanServerConnection mbsc)  {
    if (!isEnabled()) {
      return new EvaluationResult<>(HealthStatus.UNKNOW);
    }
    
    try {
      final Optional<Boolean> running = JmxHelper.isRunning(mbsc);
      if (!running.isPresent()) {
        return new EvaluationResult<Boolean>(HealthStatus.FAILURE, false, getDescription() + ": ?");
      }
      if (running.get()) {
        return new EvaluationResult<Boolean>(HealthStatus.GOOD, true);
      } else {
        return new EvaluationResult<Boolean>(HealthStatus.FAILURE, false, getDescription() + ": false");
      }
    } catch (JMException e) {
      return new EvaluationResult<Boolean>(HealthStatus.FAILURE, e);
    }
  }
  
  @Override
  public int hashCode() {
    // an arbitrary chosen value :-)
    return isEnabled() ? 42 : 43;
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof ServerRunningRule && isEnabled() == ((ServerRunningRule) other).isEnabled();
  }
  
  @Override
  public String toString() {
    return "Check if server is running";
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ServerRunningRule implements ImmutableBooleanRule {
  
  public static final ServerRunningRule INSTANCE = new ServerRunningRule();
  
  private final BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
  
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
      if (JmxHelper.isRunning(mbsc)) {
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
    return 42;
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof ServerRunningRule;
  }
  
  @Override
  public String toString() {
    return "Check if server is running";
  }
}

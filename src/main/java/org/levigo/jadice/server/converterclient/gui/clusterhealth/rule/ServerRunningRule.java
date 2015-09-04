package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class ServerRunningRule implements ImmutableBooleanRule {

  @Override
  public String getDescription() {
    return "Instance is running";
  }
  
  @Override
  public boolean getExpectedValue() {
    return true;
  }

  @Override
  public EvaluationResult<Boolean> evaluate(MBeanServerConnection mbsc)  {
    try {
      if (JmxHelper.isRunning(mbsc)) {
        return new EvaluationResult<Boolean>(HealthStatus.GOOD, true);
      } else {
        return new EvaluationResult<Boolean>(HealthStatus.FAILURE, false, getDescription() + ": false");
      }
    } catch (JMException e) {
      return new EvaluationResult<Boolean>(HealthStatus.FAILURE, false, e);
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

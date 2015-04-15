package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class ServerRunningRule implements Rule<Boolean> {

  @Override
  public String getDescription() {
    return "Instance is running";
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
}

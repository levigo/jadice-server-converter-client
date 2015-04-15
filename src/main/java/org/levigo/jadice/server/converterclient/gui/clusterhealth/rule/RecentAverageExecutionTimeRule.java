package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentAverageExecutionTimeRule implements Rule<Long> {

  private final long limit;

  public RecentAverageExecutionTimeRule(long limit) {
    this.limit = limit;
  }

  @Override
  public String getDescription() {
    return "Average execution time";
  }

  @Override
  public EvaluationResult<Long> evaluate(MBeanServerConnection mbsc) {
    try {
      final long execTime = JmxHelper.getRecentAverageExecutionTime(mbsc);
      if (execTime <= limit) {
        return new EvaluationResult<Long>(HealthStatus.GOOD, execTime);
      } else {
        return new EvaluationResult<Long>(HealthStatus.ATTENTION, execTime, getDescription() + ": " + execTime);
      }
    } catch (JMException e) {
      return new EvaluationResult<Long>(HealthStatus.FAILURE, -1L, e);
    }

  }
}

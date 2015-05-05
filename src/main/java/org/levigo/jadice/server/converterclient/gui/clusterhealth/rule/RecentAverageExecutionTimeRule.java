package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentAverageExecutionTimeRule implements NumericRule<Long> {

  private final long limit;

  public RecentAverageExecutionTimeRule(long limit) {
    this.limit = limit;
  }

  @Override
  public String getDescription() {
    return "Average execution time";
  }
  
  @Override
  public Long getLimit() {
    return limit;
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

  @Override
  public int hashCode() {
    return (int) (limit ^ (limit >>> 32));
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecentAverageExecutionTimeRule && ((RecentAverageExecutionTimeRule) other).limit == this.limit;
  }
}

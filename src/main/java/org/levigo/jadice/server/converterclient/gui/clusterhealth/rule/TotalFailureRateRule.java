package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class TotalFailureRateRule implements NumericRule<Float> {

  private final float limit;

  public TotalFailureRateRule(float limit) {
    this.limit = limit;
  }

  @Override
  public String getDescription() {
    return "Total failure rate";
  }
  
  @Override
  public Float getLimit() {
    return limit;
  }

  @Override
  public EvaluationResult<Float> evaluate(MBeanServerConnection mbsc) {
    try {
      final float rate = JmxHelper.getTotalFailureRate(mbsc);
      if (rate <= limit) {
        return new EvaluationResult<Float>(HealthStatus.GOOD, rate);
      } else {
        return new EvaluationResult<Float>(HealthStatus.ATTENTION, rate, getDescription() + ": " + rate);
      }
    } catch (JMException e) {
      return new EvaluationResult<Float>(HealthStatus.FAILURE, Float.NaN, e);
    }
  }
  
  @Override
  public int hashCode() {
    return Float.floatToIntBits(limit);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TotalFailureRateRule && ((TotalFailureRateRule) other).limit == this.limit;
  }
}

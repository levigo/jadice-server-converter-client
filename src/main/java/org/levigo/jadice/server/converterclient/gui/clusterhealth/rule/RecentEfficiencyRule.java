package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentEfficiencyRule implements NumericRule<Float> {

  private final float limit;

  public RecentEfficiencyRule(float limit) {
    this.limit = limit;
  }

  @Override
  public String getDescription() {
    return "Recent efficiency";
  }
  
  @Override
  public Float getLimit() {
    return limit;
  }

  @Override
  public EvaluationResult<Float> evaluate(MBeanServerConnection mbsc) {
    try {
      final float eff = JmxHelper.getEfficiency10Min(mbsc);
      if (eff <= limit) {
        return new EvaluationResult<Float>(HealthStatus.GOOD, eff);
      } else {
        return new EvaluationResult<Float>(HealthStatus.ATTENTION, eff, getDescription() + ": " + eff);
      }
    } catch (JMException e) {
      return new EvaluationResult<Float>(HealthStatus.FAILURE, Float.NaN, e);
    }
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class TotalFailureRateRule implements NumericRule<Float> {

  private final FloatProperty limit;

  public TotalFailureRateRule(float limit) {
    this.limit = new SimpleFloatProperty(limit);
  }

  @Override
  public String getDescription() {
    return "Total failure rate";
  }
  
  @Override
  public Property<Number> limitProperty() {
    return limit;
  }
  
  @Override
  public EvaluationResult<Float> evaluate(MBeanServerConnection mbsc) {
    try {
      final float rate = JmxHelper.getTotalFailureRate(mbsc);
      if (rate <= limit.get()) {
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
    return limit.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TotalFailureRateRule && ((TotalFailureRateRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Failure Rate of %f", getLimit());
  }
}

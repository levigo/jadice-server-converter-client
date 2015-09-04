package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentFailureRateRule implements NumericRule<Float> {

  private final FloatProperty limit;

  public RecentFailureRateRule(float limit) {
    this.limit = new SimpleFloatProperty(limit);
  }

  @Override
  public String getDescription() {
    return "Recent failure rate";
  }
  
  @Override
  public Property<Number> limitProperty() {
    return limit;
  }
  
  @Override
  public Float getLimit() {
    return limit.get();
  }
  
  @Override
  public void setLimit(Float value) {
    limit.set(value);
  }

  @Override
  public EvaluationResult<Float> evaluate(MBeanServerConnection mbsc) {
    try {
      final float rate = JmxHelper.getRecentFailureRate(mbsc);
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
    return other instanceof RecentFailureRateRule && ((RecentFailureRateRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Recent Failure Rate of %f", getLimit());
  }
}

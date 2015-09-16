package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class RecentFailureRateRule extends AbstractNumericRule<Float> {

  private final Property<Float> limit;

  public RecentFailureRateRule(float limit) {
    this.limit = new SimpleObjectProperty<>(limit);
  }

  @Override
  public String getDescription() {
    return "Recent failure rate";
  }
  
  @Override
  public Property<Float> limitProperty() {
    return limit;
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
  
  @Override
  protected ExceptionalFunction<MBeanServerConnection, Float, JMException> jmxFunction() {
    return JmxHelper::getRecentFailureRate;
  }
}

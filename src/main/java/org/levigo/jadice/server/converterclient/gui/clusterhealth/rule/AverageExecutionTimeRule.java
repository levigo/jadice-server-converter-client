package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class AverageExecutionTimeRule extends NumericRule<Long> {
  
  private final Property<Long> limit;

  public AverageExecutionTimeRule(long limit) {
    this.limit = new SimpleObjectProperty<>(limit);
  }

  @Override
  public String getDescription() {
    return "Average execution time";
  }
  
  @Override
  public Property<Long> limitProperty() {
    return limit;
  }
  
  @Override
  public int hashCode() {
    return limit.hashCode();
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof AverageExecutionTimeRule && ((AverageExecutionTimeRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Average Excecution Time of %d ms", getLimit());
  }

  @Override
  protected ExceptionalFunction<MBeanServerConnection, Long, JMException> jmxFunction() {
    return JmxHelper::getAverageExecutionTime;
  }
}

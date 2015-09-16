package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class NumericRule<T extends Number & Comparable<T>> implements Rule<T>  {
  
  interface ExceptionalFunction<T, R, E extends Exception>  {
    R apply(T t) throws E;
  }
  
  private BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
  
//  abstract public EvaluationResult<T> evaluate(MBeanServerConnection mbsc);
  
  public EvaluationResult<T> evaluate(MBeanServerConnection mbsc) {
    try {
      final T currentValue = jmxFunction().apply(mbsc);
      if (getLimit().compareTo(currentValue) <= 0) { // i.e. getLimit() <= execTime
        return new EvaluationResult<T>(HealthStatus.GOOD, currentValue);
      } else {
        return new EvaluationResult<T>(HealthStatus.ATTENTION, currentValue, getDescription() + ": " + currentValue);
      }
    } catch (JMException e) {
      return new EvaluationResult<T>(HealthStatus.FAILURE, e);
    }
  }

  
  abstract Property<T> limitProperty();
  
  @Override
  public BooleanProperty enabledProperty() {
    return enabledProperty;
  }
  
  public T getLimit() {
    return limitProperty().getValue();
  }
  
  public void setLimit(T value) {
    limitProperty().setValue(value);
  }
  
  abstract protected ExceptionalFunction<MBeanServerConnection, T, JMException> jmxFunction();
  
}

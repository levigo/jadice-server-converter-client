package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Optional;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class AbstractNumericRule<T extends Number & Comparable<T>> implements Rule<T>  {
  
  @FunctionalInterface
  interface JMXFunction<T>  {
    Optional<T> evaluate(MBeanServerConnection mbsc) throws JMException;
  }
  
  private final Property<T> limitProperty;
  
  private final JMXFunction<T> jmxFunction;
  
  private final BooleanProperty enabledProperty = new SimpleBooleanProperty(true);

  protected AbstractNumericRule(T initalLimit, JMXFunction<T> jmxFunction) {
   this.limitProperty = new SimpleObjectProperty<>(initalLimit);
   this.jmxFunction = jmxFunction;
  }
  
  public EvaluationResult<T> evaluate(MBeanServerConnection mbsc) {
    if (!isEnabled()) {
      return new EvaluationResult<T>(HealthStatus.UNKNOW);
    }
    try {
      final Optional<T> currentValue = jmxFunction.evaluate(mbsc);
      if (!currentValue.isPresent()) {
        return new EvaluationResult<T>(HealthStatus.ATTENTION, getDescription() + ": ?");
      }
      T val = currentValue.get();
      if (getLimit().compareTo(val) >= 0) { // i.e. getLimit() >= execTime
        return new EvaluationResult<T>(HealthStatus.GOOD, val);
      } else {
        return new EvaluationResult<T>(HealthStatus.ATTENTION, val, getDescription() + ": " + val);
      }
    } catch (JMException e) {
      return new EvaluationResult<T>(HealthStatus.FAILURE, e);
    }
  }
  
  public final Property<T> limitProperty() {
    return limitProperty;
  }
  
  public final T getLimit() {
    return limitProperty().getValue();
  }
  
  public final void setLimit(T value) {
    limitProperty().setValue(value);
  }

  @Override
  public final BooleanProperty enabledProperty() {
    return enabledProperty;
  }
}

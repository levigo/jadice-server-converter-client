package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class NumericRule<T extends Number> implements Rule<T>  {
  
  interface ExceptionalFunction<T, R, E extends Exception>  {
    R apply(T t) throws E;
  }
  
  private BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
  
  abstract public EvaluationResult<T> evaluate(MBeanServerConnection mbsc);
  
  abstract Property<Number> limitProperty();
  
  @Override
  public BooleanProperty enabledProperty() {
    return enabledProperty;
  }
  
  public Number getLimit() {
    return limitProperty().getValue();
  }
  
  public void setLimit(Number value) {
    limitProperty().setValue(value);
  }
  
  abstract protected ExceptionalFunction<MBeanServerConnection, T, JMException> jmxFunction();
  
}

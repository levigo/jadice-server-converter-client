package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class NumericRule<T extends Number> implements Rule<T>  {
  
  private BooleanProperty enabledProperty = new SimpleBooleanProperty(true);
  
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
  
}

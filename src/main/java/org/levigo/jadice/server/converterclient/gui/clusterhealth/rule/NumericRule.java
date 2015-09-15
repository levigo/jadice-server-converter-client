package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.Property;

public abstract class NumericRule<T extends Number> implements Rule<T>  {
  
  abstract Property<Number> limitProperty();
  
  public Number getLimit() {
    return limitProperty().getValue();
  }
  
  public void setLimit(Number value) {
    limitProperty().setValue(value);
  }
  
}

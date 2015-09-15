package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.Property;

public interface NumericRule<T extends Number> extends Rule<T>  {
  
  Property<Number> limitProperty();
  
  default Number getLimit() {
    return limitProperty().getValue();
  }
  
  default void setLimit(Number value) {
    limitProperty().setValue(value);
  }
  
}

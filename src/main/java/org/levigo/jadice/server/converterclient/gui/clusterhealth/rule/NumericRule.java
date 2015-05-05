package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javafx.beans.property.Property;

public interface NumericRule<T extends Number> extends Rule<T>  {
  
  Property<Number> limitProperty();
  
  T getLimit();
  
  void setLimit(T value);
  
}

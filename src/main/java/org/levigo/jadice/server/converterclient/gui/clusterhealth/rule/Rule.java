package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.MBeanServerConnection;

import javafx.beans.property.BooleanProperty;

public interface Rule<T> {
  
  String getDescription();
  
  EvaluationResult<T> evaluate(MBeanServerConnection mbsc);
  
  BooleanProperty enabledProperty();
  
  default boolean isEnabled() {
    return enabledProperty().get();
  }
  
  default void setEnabled(boolean enabled) {
    enabledProperty().set(enabled);
  }
  
}
package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

public interface NumericRule<T extends Number> extends Rule<T>  {
  
  T getLimit();
  
}

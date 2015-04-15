package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import javax.management.MBeanServerConnection;

public interface Rule<T> {
  
  String getDescription();
  
  EvaluationResult<T> evaluate(MBeanServerConnection mbsc);
}

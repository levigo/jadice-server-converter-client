package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Optional;

import javax.management.JMException;
import javax.management.MBeanServerConnection;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

public interface Rule {
  
  public enum Severity {
    WARNING,
    FATAL;
  }
  
  String getDescription();
  
  Optional<String> check(MBeanServerConnection mbsc) throws JMException;
  
  default HealthStatus getStatus() {
    return HealthStatus.ATTENTION;
  }

}

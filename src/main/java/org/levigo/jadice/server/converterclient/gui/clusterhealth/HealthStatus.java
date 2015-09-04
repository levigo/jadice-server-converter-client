package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.util.Comparator;

public enum HealthStatus {
  
  UNKNOW (-1),
  GOOD (0),
  ATTENTION (5),
  FAILURE (10);
  
  private final int severity;
  
  private HealthStatus(int severity) {
    this.severity = severity;
  }
  
  /**
   * Method can be used as {@link Comparator} where the most severe {@link HealthStatus} appears first.
   */
  public static int severeFirst(HealthStatus a, HealthStatus b) {
    return -Integer.compare(a.severity, b.severity);
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import static java.util.Objects.requireNonNull;

import java.util.function.BinaryOperator;

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
   * Return the more severe status of the two given ones. Can be used as {@link BinaryOperator}.
   * 
   * @param a an argument
   * @param b another argument
   * @return the more severe status of both
   */
  public static HealthStatus maxSevere(HealthStatus a, HealthStatus b) {
    return (requireNonNull(a).severity >= requireNonNull(b).severity) ? a : b;
  }
}

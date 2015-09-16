package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentFailureRateRule extends AbstractNumericRule<Float> {

  public RecentFailureRateRule(float limit) {
    super(limit, JmxHelper::getRecentFailureRate);
  }

  @Override
  public String getDescription() {
    return "Recent failure rate";
  }
  
  @Override
  public int hashCode() {
    return limitProperty().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecentFailureRateRule && ((RecentFailureRateRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Recent Failure Rate of %f", getLimit());
  }
}

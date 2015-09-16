package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentEfficiencyRule extends AbstractNumericRule<Float> {

  public RecentEfficiencyRule(float initialLimit) {
    super(initialLimit, JmxHelper::getEfficiency10Min);
  }

  @Override
  public String getDescription() {
    return "Recent efficiency";
  }
  
  @Override
  public int hashCode() {
    return limitProperty().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecentEfficiencyRule && ((RecentEfficiencyRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Recent Efficiency of %f", getLimit());
  }
}

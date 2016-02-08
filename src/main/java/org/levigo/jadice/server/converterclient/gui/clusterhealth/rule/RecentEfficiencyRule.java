package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentEfficiencyRule extends AbstractNumericRule<Float> {

  public RecentEfficiencyRule(float initialLimit, boolean isEnabled) {
    super(initialLimit, JmxHelper::getEfficiency10Min, isEnabled);
  }

  @Override
  public String getDescription() {
    return "Recent efficiency";
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof RecentEfficiencyRule && super.equals(other);
  }
  
  @Override
  public String toString() {
    return String.format("Recent Efficiency of %f", getLimit());
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class TotalFailureRateRule extends AbstractNumericRule<Float> {

  public TotalFailureRateRule(float limit, boolean isEnabled) {
    super(limit, JmxHelper::getTotalFailureRate, isEnabled);
  }

  @Override
  public String getDescription() {
    return "Total failure rate";
  }
  
  @Override
  public int hashCode() {
    return limitProperty().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof TotalFailureRateRule && ((TotalFailureRateRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Failure Rate of %f", getLimit());
  }
}

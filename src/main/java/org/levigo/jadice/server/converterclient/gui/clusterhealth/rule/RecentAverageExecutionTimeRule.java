package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentAverageExecutionTimeRule extends AbstractNumericRule<Long> {


  public RecentAverageExecutionTimeRule(long initalLimit) {
    super(initalLimit, JmxHelper::getRecentAverageExecutionTime);
  }

  @Override
  public String getDescription() {
    return "Recent average execution time";
  }
  
  @Override
  public int hashCode() {
    return limitProperty().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof RecentAverageExecutionTimeRule && ((RecentAverageExecutionTimeRule) other).getLimit().equals(this.getLimit());
  }
  
  @Override
  public String toString() {
    return String.format("Recent Average Excecution Time of %d ms", getLimit());
  }
}

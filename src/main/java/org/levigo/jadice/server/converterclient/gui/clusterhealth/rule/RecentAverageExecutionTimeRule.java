package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class RecentAverageExecutionTimeRule extends AbstractNumericRule<Long> {


  public RecentAverageExecutionTimeRule(long initalLimit, boolean isEnabled) {
    super(initalLimit, JmxHelper::getRecentAverageExecutionTime, isEnabled);
  }

  @Override
  public String getDescription() {
    return "Recent average execution time";
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof RecentAverageExecutionTimeRule && super.equals(other);
  }
  
  @Override
  public String toString() {
    return String.format("Recent Average Excecution Time of %d ms", getLimit());
  }
}

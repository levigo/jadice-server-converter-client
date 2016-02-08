package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.JmxHelper;

public class AverageExecutionTimeRule extends AbstractNumericRule<Long> {
  

  public AverageExecutionTimeRule(long initalLimit, boolean isEnabled) {
    super(initalLimit, JmxHelper::getAverageExecutionTime, isEnabled);
  }

  @Override
  public String getDescription() {
    return "Average execution time";
  }
  
  @Override
  public boolean equals(Object other) {
    return other instanceof AverageExecutionTimeRule && super.equals(other);
  }
  
  @Override
  public String toString() {
    return String.format("Average Excecution Time of %d ms", getLimit());
  }
}

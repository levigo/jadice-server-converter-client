package org.levigo.jadice.server.converterclient.gui.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.apache.log4j.Logger;

public class PerformanceInfoDTO {
  
  private static final Logger LOGGER = Logger.getLogger(PerformanceInfoDTO.class);

  private static final String COMPLETED_TASK_COUNT_ATTRIBUTS = "CompletedTaskCount";

  private static final String EFFICIENCY_ATTRIBUTE = "Efficiency10Min";

  private static final String AVG_EXECUTION_TIME_ATTRIBUTE = "AverageExecutionTime";

  private static final String MIN_EXECUTION_TIME_ATTRIBUTE = "RecentMinimumExecutionTime";

  private static final String MAX_EXECUTION_TIME_ATTRIBUTE = "RecentMaximumExecutionTime";

  private static final String ABORT_RATE_ATTRIBUTE = "TotalAbortRate";

  private static final String FAILURE_RATE_ATTRIBUTE = "TotalFailureRate";

  private static final String ACTIVE_JOB_COUNT_ATTRIBUTE = "ActiveCount";

  private static final String MAX_JOB_COUNT_ATTRIBUTE = "MaximumPoolSize";

  public static final String[] ATTRIBUTE_NAMES_SERVER_STATISTICS = new String[]{
      COMPLETED_TASK_COUNT_ATTRIBUTS, EFFICIENCY_ATTRIBUTE, AVG_EXECUTION_TIME_ATTRIBUTE, MAX_EXECUTION_TIME_ATTRIBUTE,
      MIN_EXECUTION_TIME_ATTRIBUTE, ABORT_RATE_ATTRIBUTE, FAILURE_RATE_ATTRIBUTE
      
  };
  
  public static final String[] ATTRIBUTE_NAMES_JOB_SCHEDULER = new String[] {
    ACTIVE_JOB_COUNT_ATTRIBUTE, MAX_JOB_COUNT_ATTRIBUTE
  };

  public long completedTaskCount;
  public float efficiency;
  public long avgExecutionTime;
  public long maxExecutionTime;
  public long minExecutionTime;
  public float abortRate;
  public float failureRate;

  public int maxJobCount;

  public int activeJobCount;


  public static PerformanceInfoDTO parseAttributsList(AttributeList attributes) {
    final PerformanceInfoDTO result = new PerformanceInfoDTO();
    for (Attribute attr : attributes.asList()) {
      switch (attr.getName()) {
        case COMPLETED_TASK_COUNT_ATTRIBUTS:
          result.completedTaskCount = (Long) attr.getValue();
          break;
        case EFFICIENCY_ATTRIBUTE:
          result.efficiency = (Float) attr.getValue();
          break;
        case AVG_EXECUTION_TIME_ATTRIBUTE:
          result.avgExecutionTime = (Long) attr.getValue();
          break;
        case MAX_EXECUTION_TIME_ATTRIBUTE:
          result.maxExecutionTime = (Long) attr.getValue();
          break;
        case MIN_EXECUTION_TIME_ATTRIBUTE:
          result.minExecutionTime = (Long) attr.getValue();
          break;
        case ABORT_RATE_ATTRIBUTE:
          result.abortRate = (Float) attr.getValue();
          break;
        case FAILURE_RATE_ATTRIBUTE:
          result.failureRate = (Float) attr.getValue();
          break;
        case ACTIVE_JOB_COUNT_ATTRIBUTE:
          result.activeJobCount = (Integer) attr.getValue();
          break;
        case MAX_JOB_COUNT_ATTRIBUTE:
          result.maxJobCount = (Integer) attr.getValue();
          break;
        default:
          LOGGER.error("Unsupported Attribute:" + attr);
          break;
      }
    }
    return result;
  }

}

package org.levigo.jadice.server.converterclient.gui.jmx;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.levigo.jadice.server.Job.State;

public class JobExecutionDataItem extends TimeSeriesDataItem {

  private static final long serialVersionUID = 1L;
  
  private final JobStateEventDTO event;

  public JobExecutionDataItem(RegularTimePeriod period, JobStateEventDTO event) {
    super(period, event.age);
    this.event = event;
  }
  
  public String getID() {
    return event.id;
  }
  
  public String getJobType() {
    return event.type;
  }
  
  public State getState() {
    return event.state;
  }
}

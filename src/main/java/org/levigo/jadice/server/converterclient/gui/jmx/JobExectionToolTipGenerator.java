package org.levigo.jadice.server.converterclient.gui.jmx;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

public class JobExectionToolTipGenerator implements XYToolTipGenerator {
  
  private static final String LABEL_FORMAT = "Job %s\n(type %s)\nState: %s";
  
  private final TimeSeries dataset;
  
  public JobExectionToolTipGenerator(TimeSeries dataset) {
    this.dataset = dataset;
  }

  @Override
  public String generateToolTip(XYDataset dataset, int series, int item) {
    if (this.dataset.getItemCount() <= item) {
      return null;
    }
    
    final TimeSeriesDataItem dataItem = this.dataset.getDataItem(item);
    
    // Sanity check if our 'own' dataset matches the given one 
    if (!(dataItem instanceof JobExecutionDataItem)
        || dataItem.getPeriod().getFirstMillisecond() != dataset.getXValue(series, item)) {
      return null;
    }
    JobExecutionDataItem jedi = (JobExecutionDataItem) dataItem;
    return String.format(LABEL_FORMAT, jedi.getID(), jedi.getJobType(), jedi.getState());
  }    
}

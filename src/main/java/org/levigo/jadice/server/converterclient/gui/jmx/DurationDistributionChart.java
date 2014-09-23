package org.levigo.jadice.server.converterclient.gui.jmx;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;

public class DurationDistributionChart extends ChartViewer implements Chart {
  
  private static final Logger LOGGER = Logger.getLogger(DurationDistributionChart.class);

  private static final int HISTOGRAMM_BIN_SIZE = 1000;

  private final SimpleHistogramDataset histogramDataset;
	
	private long maxBin = 0;


	public DurationDistributionChart() {
		super(ChartFactory.createHistogram("duration distribution", "ms", null, new SimpleHistogramDataset(1), PlotOrientation.VERTICAL, 
				false, false, false));
		
		// SimpleHistogramDataset that was used in super constr.
		histogramDataset = (SimpleHistogramDataset) getChart().getXYPlot().getDataset(0);
	}

  @Override
  public void addObservation(JobStateEventDTO event) {
    if (event.state.isTerminalState()) {
      addDuration(event.age);
    }
    
  }
	
	@Override
	public void updatePerformanceInfo(PerformanceInfoDTO performanceInfo) {
	  // Don't care about it
	}

	public void addDuration(long duration) {
		
		histogramDataset.setAdjustForBinSize(true);
		while (duration > this.maxBin) {
			try {
			histogramDataset.addBin(new SimpleHistogramBin(maxBin, maxBin+HISTOGRAMM_BIN_SIZE-1));
			maxBin += HISTOGRAMM_BIN_SIZE;
			} catch (Throwable t) {
				LOGGER.error("Could not create histogram bin", t);
			}
		}
		histogramDataset.addObservation(duration);
	}

	@Override
	public void clear() {
		histogramDataset.clearObservations();
		histogramDataset.removeAllBins();
		maxBin = 0;
	}
}

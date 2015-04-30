package org.levigo.jadice.server.converterclient.gui.jmx;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.awt.Color;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class PerformanceChart extends ChartViewer implements Chart {
	
  private static final Logger LOGGER = Logger.getLogger(PerformanceChart.class);

  private static final int MAX_DATA_AGE = 60; // minutes
	
	private final TimeSeries durations = new TimeSeries("duration");

	private final TimeSeries efficiency = new TimeSeries("efficiency");
	
	private final TimeSeries maxDurations = new TimeSeries("max. duration");
	
	private final TimeSeries minDurations = new TimeSeries("min. duration");
	
	private final TimeSeries avgDurations = new TimeSeries("avg. duration");
	
	public PerformanceChart() {
	  super(ChartFactory.createTimeSeriesChart("performance", "time", "ms", new TimeSeriesCollection(), true, false, false));
		// Method requires milliseconds
		durations.setMaximumItemAge(MINUTES.toMillis(MAX_DATA_AGE));
		efficiency.setMaximumItemAge(MINUTES.toMillis(MAX_DATA_AGE));
		maxDurations.setMaximumItemAge(MINUTES.toMillis(MAX_DATA_AGE));
		minDurations.setMaximumItemAge(MINUTES.toMillis(MAX_DATA_AGE));
		avgDurations.setMaximumItemAge(MINUTES.toMillis(MAX_DATA_AGE));
		
		
		// TimeSeriesCollection that was used in super constr.
		final TimeSeriesCollection minMaxCollection = (TimeSeriesCollection) getChart().getXYPlot().getDataset(0);
		minMaxCollection.addSeries(maxDurations);
		minMaxCollection.addSeries(minDurations);
		
    getChart().setAntiAlias(true);
		final XYPlot plot = getChart().getXYPlot();
		final Color differencePaint = new Color(255,00,000,60);
		XYDifferenceRenderer differenceRenderer = new XYDifferenceRenderer(differencePaint, differencePaint, false);
		differenceRenderer.setRoundXCoordinates(true);
		differenceRenderer.setAutoPopulateSeriesOutlinePaint(false);
		differenceRenderer.setAutoPopulateSeriesFillPaint(false);
		differenceRenderer.setAutoPopulateSeriesPaint(false);
		differenceRenderer.setAutoPopulateSeriesShape(false);
		differenceRenderer.setAutoPopulateSeriesStroke(false);
		differenceRenderer.setPositivePaint(differencePaint);
		differenceRenderer.setNegativePaint(differencePaint);
		differenceRenderer.setBaseFillPaint(differencePaint);
		
		differenceRenderer.setBaseSeriesVisible(false);
    plot.setRenderer(0, differenceRenderer);
    
    final TimeSeriesCollection durationColletion = new TimeSeriesCollection(durations);
    XYItemRenderer dotRenderer = new XYLineAndShapeRenderer(false, true);
    plot.setDataset(1, durationColletion);
    plot.mapDatasetToRangeAxis(1, 0);
    plot.setRenderer(1, dotRenderer);
    
    final TimeSeriesCollection avgColletion = new TimeSeriesCollection(avgDurations);
    XYSplineRenderer lineRenderer = new XYSplineRenderer(2);
    lineRenderer.setBaseShapesVisible(false);
    plot.setDataset(2, avgColletion);
    plot.mapDatasetToRangeAxis(2, 0);
    plot.setRenderer(2, lineRenderer);
    
    final NumberAxis axis2 = new NumberAxis("efficiency");
    axis2.setAutoRangeIncludesZero(true);
    axis2.setLowerBound(0.0);
    axis2.setAutoRange(true);
    plot.setRangeAxis(1, axis2);
    plot.setDataset(3, new TimeSeriesCollection(efficiency));
    plot.mapDatasetToRangeAxis(3, 1);
    XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
    plot.setRenderer(3, renderer);
	}
	
	@Override
	public void addObservation(JobStateEventDTO event) {
	  if (event.state.isTerminalState()) {
	    addDuration(event.age);
	  }
	}

	public void addDuration(long duration) {
		try {
      FixedMillisecond now = new FixedMillisecond();
      durations.addOrUpdate(now, duration);
    } catch (NullPointerException npe) {
      LOGGER.error("Could not render duration", npe);
      clear();
    }
	}

  @Override
	public void clear() {
		durations.clear();
		efficiency.clear();
		minDurations.clear();
		maxDurations.clear();
		avgDurations.clear();
	}
	
	@Override
	public void updatePerformanceInfo(PerformanceInfoDTO performanceInfo) {
	  FixedMillisecond now = new FixedMillisecond();
	  minDurations.addOrUpdate(now, performanceInfo.minExecutionTime);
	  maxDurations.addOrUpdate(now, performanceInfo.maxExecutionTime);
	  avgDurations.addOrUpdate(now, performanceInfo.avgExecutionTime);
	  efficiency.addOrUpdate(now, performanceInfo.efficiency);
	}

}

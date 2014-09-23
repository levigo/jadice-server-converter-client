package org.levigo.jadice.server.converterclient.gui.jmx;


public interface Chart {

	void addObservation(JobStateEventDTO event);
	
	void updatePerformanceInfo(PerformanceInfoDTO performanceInfo);
	
	void clear();
	
}

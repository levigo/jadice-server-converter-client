package org.levigo.jadice.server.converterclient.gui.jmx;


// FIXME: Inject i18n resources into subclasses 
public interface Chart {

	void addObservation(JobStateEventDTO event);
	
	void updatePerformanceInfo(PerformanceInfoDTO performanceInfo);
	
	void clear();
	
}

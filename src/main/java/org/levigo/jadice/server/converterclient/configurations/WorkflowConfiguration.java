package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;

public interface WorkflowConfiguration {
	
	String getID();
	
	String getDescription();
	
	void configureWorkflow(Job job) throws Exception;
}

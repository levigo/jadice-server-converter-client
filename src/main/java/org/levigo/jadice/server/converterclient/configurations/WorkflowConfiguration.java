package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;

public interface WorkflowConfiguration {
	
	String getID();
	String getDescription();
	Job configureWorkflow(JobFactory jobFactory) throws Exception;
}

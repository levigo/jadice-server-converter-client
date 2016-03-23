package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class ShuntThroughConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws JobException {
		Job j = jobFactory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "Shunt the given input through the server and back to the client";
	}

	public String getID() {
		return "shunt";
	}
}

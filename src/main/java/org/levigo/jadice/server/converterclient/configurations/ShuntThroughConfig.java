package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class ShuntThroughConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		job.attach(new StreamInputNode() //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "Shunt the given input through the server and back to the client";
	}

	public String getID() {
		return "shunt";
	}
}

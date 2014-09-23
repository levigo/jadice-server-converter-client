package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.msoffice.MSPowerpointNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class PowerpointToPdfAConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		final MSPowerpointNode powerpointNode = new MSPowerpointNode();
		powerpointNode.setTargetMimeType("application/pdf;version=A-1");
    j.attach(new StreamInputNode() //
				.appendSuccessor(powerpointNode) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "MS PowerPoint file to PDF/A-1";
	}

	public String getID() {
		return "ppt2pdfA";
	}

}

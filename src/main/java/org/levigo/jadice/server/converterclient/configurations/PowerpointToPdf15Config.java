package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.msoffice.MSPowerpointNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class PowerpointToPdf15Config implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		final MSPowerpointNode powerpointNode = new MSPowerpointNode();
		powerpointNode.setTargetMimeType("application/pdf;version=1.5");

		job.attach(new StreamInputNode() //
				.appendSuccessor(powerpointNode) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "MS PowerPoint file to PDF 1.5";
	}

	public String getID() {
		return "ppt2pdf15";
	}

}

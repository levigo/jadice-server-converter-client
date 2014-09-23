package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.msoffice.MSVisioNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class VisioToPdf15Config implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		final MSVisioNode visioNode = new MSVisioNode();
		visioNode.setTargetMimeType("application/pdf;version=1.5");
    j.attach(new StreamInputNode() //
				.appendSuccessor(visioNode) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "MS Visio file to PDF 1.5";
	}

	public String getID() {
		return "visio2pdf15";
	}

}

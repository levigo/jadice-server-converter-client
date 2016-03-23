package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.msoffice.MSVisioNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class VisioToPdfAConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) throws Exception {
		final MSVisioNode visioNode = new MSVisioNode();
		visioNode.setTargetMimeType("application/pdf;version=A-1");

		job.attach(new StreamInputNode() //
				.appendSuccessor(visioNode) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "MS Visio file to PDF/A-1";
	}

	public String getID() {
		return "visio2pdfA";
	}

}

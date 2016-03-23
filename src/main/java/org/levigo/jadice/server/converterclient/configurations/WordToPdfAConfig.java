package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.msoffice.MSWordNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class WordToPdfAConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		final MSWordNode wordNode = new MSWordNode();
		wordNode.setTargetMimeType("application/pdf;version=A-1");

		job.attach(new StreamInputNode() //
				.appendSuccessor(wordNode) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "MS Word file to PDF/A-1";
	}

	public String getID() {
		return "word2pdfA";
	}

}

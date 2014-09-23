package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.msoffice.MSWordNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class WordToPdfAConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		final MSWordNode wordNode = new MSWordNode();
		wordNode.setTargetMimeType("application/pdf;version=A-1");
    j.attach(new StreamInputNode() //
				.appendSuccessor(wordNode) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "MS Word file to PDF/A-1";
	}

	public String getID() {
		return "word2pdfA";
	}

}

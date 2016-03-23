package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.javamail.MessageRFC822Node;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class MailPreviewConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws URISyntaxException, JobException {
		
		ScriptNode scriptNode = new ScriptNode();
		URI scriptLocation = new URI(
				"resource:email-conversion/EmailPreviewConversion.groovy");
		scriptNode.setScript(scriptLocation);

		Job j = jobFactory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(new MessageRFC822Node()) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "eMail to PDF (Preview only)";
	}

	public String getID() {
		return "mail-preview";
	}
}

package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobCreationException;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.javamail.MessageRFC822Node;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class MailConverterConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory)
			throws URISyntaxException, JobCreationException {
		
		ScriptNode scriptNode = new ScriptNode();
		URI scriptLocation = new URI(
				"resource:email-conversion/EmailConversion.groovy");
		scriptNode.setScript(scriptLocation);
		scriptNode.getParameters().put("allowExternalHTTPResolution", false);
		scriptNode.getParameters().put("preferPlainTextBody", false);
		scriptNode.getParameters().put("showAllAlternativeBody", false);
		scriptNode.getParameters().put("unhandledAttachmentAction", "failure");

		Job j = jobFactory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(new MessageRFC822Node()) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "eMail to PDF (LibreOffice)";
	}

	public String getID() {
		return "mail2pdf (lo)";
	}
}

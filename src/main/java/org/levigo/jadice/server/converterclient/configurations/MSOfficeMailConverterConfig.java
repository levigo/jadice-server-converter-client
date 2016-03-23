package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.javamail.MessageRFC822Node;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class MSOfficeMailConverterConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) throws URISyntaxException {
		final ScriptNode scriptNode = new ScriptNode();
		scriptNode.setScript(new URI("resource:email-conversion/EmailConversionViaMSOffice.groovy"));
		scriptNode.getParameters().put("allowExternalHTTPResolution", false);
		scriptNode.getParameters().put("preferPlainTextBody", false);
		scriptNode.getParameters().put("showAllAlternativeBody", false);
		scriptNode.getParameters().put("unhandledAttachmentAction", "failure");

		job.attach(new StreamInputNode() //
				.appendSuccessor(new MessageRFC822Node()) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "eMail to PDF (MS Office)";
	}

	public String getID() {
		return "mail2pdf (msoffice)";
	}
}

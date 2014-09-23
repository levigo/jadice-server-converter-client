package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.javamail.TNEFNode;
import com.levigo.jadice.server.javamail.TNEFNode.InputFormat;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class MSGConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		TNEFNode tnefNode = new TNEFNode();
		tnefNode.setInputFormat(InputFormat.MSG);

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
				.appendSuccessor(tnefNode) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));

		return j;
	}

	public String getDescription() {
		return "MS Outlook MSG to PDF";
	}

	public String getID() {
		return "msg2pdf";
	}
}

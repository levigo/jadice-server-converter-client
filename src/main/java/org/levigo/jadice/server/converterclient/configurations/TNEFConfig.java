package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.javamail.TNEFNode;
import com.levigo.jadice.server.javamail.TNEFNode.InputFormat;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class TNEFConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) throws URISyntaxException {
		TNEFNode tnefNode = new TNEFNode();
		tnefNode.setInputFormat(InputFormat.TNEF);

		ScriptNode scriptNode = new ScriptNode();
		scriptNode.setScript(new URI("resource:email-conversion/EmailConversion.groovy"));
		scriptNode.getParameters().put("allowExternalHTTPResolution", false);
		scriptNode.getParameters().put("preferPlainTextBody", false);
		scriptNode.getParameters().put("showAllAlternativeBody", false);
		scriptNode.getParameters().put("unhandledAttachmentAction", "failure");

		job.attach(new StreamInputNode()//
				.appendSuccessor(tnefNode) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "Outlook \"winmail.dat\" files to PDF";
	}

	public String getID() {
		return "tnef2pdf";
	}

}

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

public class MSGPreviewConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) throws URISyntaxException {
	  final TNEFNode tnefNode = new TNEFNode();
	  tnefNode.setInputFormat(InputFormat.MSG);
		
		ScriptNode scriptNode = new ScriptNode();
		scriptNode.setScript(new URI("resource:email-conversion/EmailPreviewConversion.groovy"));

		job.attach(new StreamInputNode() //
				.appendSuccessor(tnefNode) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "MS Outlook MSG to PDF (Preview only)";
	}

	public String getID() {
		return "msg-preview";
	}
}

package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.javamail.TNEFNode;
import com.levigo.jadice.server.javamail.TNEFNode.InputFormat;
import com.levigo.jadice.server.nodes.ScriptNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class MSGPreviewConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws URISyntaxException, JobException {
	  
	  final TNEFNode tnefNode = new TNEFNode();
	  tnefNode.setInputFormat(InputFormat.MSG);
		
		ScriptNode scriptNode = new ScriptNode();
		URI scriptLocation = new URI(
				"resource:email-conversion/EmailPreviewConversion.groovy");
		scriptNode.setScript(scriptLocation);

		Job j = jobFactory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(tnefNode) //
				.appendSuccessor(scriptNode) //
				.appendSuccessor(new PDFMergeNode()) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "MS Outlook MSG to PDF (Preview only)";
	}

	public String getID() {
		return "msg-preview";
	}
}

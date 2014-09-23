package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.msoffice.MSWordNode;
import com.levigo.jadice.server.msoffice.MSWordNode.WdOpenFormat;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class Html2DocxConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		final MSWordNode wordNode = new MSWordNode();
		wordNode.setOpenFormat(WdOpenFormat.WD_OPEN_FORMAT_WEB_PAGES);
		wordNode.setTargetMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    j.attach(new StreamInputNode() //
				.appendSuccessor(wordNode) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "HTML to DOCX via MS Word";
	}

	public String getID() {
		return "html2docx";
	}

}

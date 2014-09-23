package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.html.HTMLRendererNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class HTMLConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory factory) throws Exception {
		Job j = factory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(new HTMLRendererNode()) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "HTML to PDF";
	}

	public String getID() {
		return "html2pdf";
	}
}

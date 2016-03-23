package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.html.HTMLRendererNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class HTMLConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		job.attach(new StreamInputNode() //
				.appendSuccessor(new HTMLRendererNode()) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "HTML to PDF";
	}

	public String getID() {
		return "html2pdf";
	}
}

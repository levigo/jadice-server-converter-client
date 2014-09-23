package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.documentplatform.StreamAnalysisNode;
import com.levigo.jadice.server.libreoffice.server.LibreOfficeConversionNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class LibreOfficeConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		j.attach(new StreamInputNode() //
				.appendSuccessor(new StreamAnalysisNode()) //
				.appendSuccessor(new LibreOfficeConversionNode()) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "Office Documents to PDF via LibreOffice";
	}

	public String getID() {
		return "lo2pdf";
	}

}

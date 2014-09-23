package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.documentplatform.DocumentPrintNode;
import com.levigo.jadice.server.documentplatform.JadiceDocumentInfoNode.UnhandledFormatAction;
import com.levigo.jadice.server.nodes.StreamInputNode;

public class PrintConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		DocumentPrintNode printer = new DocumentPrintNode();
		printer.setUnhandledFormatAction(UnhandledFormatAction.ABORT);
    j.attach(new StreamInputNode() //
				.appendSuccessor(printer));
		return j;
	}

	public String getDescription() {
		return "Print document to default printer";
	}

	public String getID() {
		return "print";
	}

}

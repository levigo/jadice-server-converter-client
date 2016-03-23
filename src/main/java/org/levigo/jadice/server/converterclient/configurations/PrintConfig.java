package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.documentplatform.DocumentPrintNode;
import com.levigo.jadice.server.documentplatform.JadiceDocumentInfoNode.UnhandledFormatAction;
import com.levigo.jadice.server.nodes.StreamInputNode;

public class PrintConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		DocumentPrintNode printer = new DocumentPrintNode();
		printer.setUnhandledFormatAction(UnhandledFormatAction.ABORT);

		job.attach(new StreamInputNode() //
				.appendSuccessor(printer));
	}

	public String getDescription() {
		return "Print document to default printer";
	}

	public String getID() {
		return "print";
	}

}

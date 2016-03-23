package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.DynamicPipelineNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class X2PdfConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		job.attach(new StreamInputNode() //
				.appendSuccessor(new DynamicPipelineNode()) //
				.appendSuccessor(new PDFMergeNode()) ///
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "Convert to PDF with format detection";
	}

	public String getID() {
		return "x2pdf";
	}

}

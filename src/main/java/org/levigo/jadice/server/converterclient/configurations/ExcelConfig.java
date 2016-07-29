package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.msoffice.MSExcelNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class ExcelConfig implements WorkflowConfiguration {

	public void configureWorkflow(Job job) {
		job.attach(new StreamInputNode() //
				.appendSuccessor(new MSExcelNode()) //
				.appendSuccessor(new StreamOutputNode()));
	}

	public String getDescription() {
		return "MS Excel file to PDF";
	}

	public String getID() {
		return "excel2pdf";
	}

}

package org.levigo.jadice.server.converterclient.configurations;

import java.util.Collections;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.msoffice.MSProjectNode;
import com.levigo.jadice.server.msoffice.MSProjectNode.View;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class MSProjectConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		final MSProjectNode msProject = new MSProjectNode();
		msProject.setViewsToExport(Collections.singletonList(View.PJ_VIEW_GANTT));
    j.attach(new StreamInputNode() //
				.appendSuccessor(msProject) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "MS Project file to PDF (Gantt diagramm only)";
	}

	public String getID() {
		return "project2pdf";
	}

}

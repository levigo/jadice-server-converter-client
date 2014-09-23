package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.nodes.DynamicPipelineNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class MSOfficeConfig implements WorkflowConfiguration {

  public Job configureWorkflow(JobFactory jobFactory) throws Exception {
    final Job j = jobFactory.createJob();
    final DynamicPipelineNode msOfficePipeline = new DynamicPipelineNode();
    msOfficePipeline.setRuleset(new URI("resource:/dynamic-pipeline-rules/msoffice.xml"));
    j.attach(new StreamInputNode() //
      .appendSuccessor(msOfficePipeline) //
      .appendSuccessor(new StreamOutputNode()));
    return j;
  }

  public String getDescription() {
    return "Office Documents to PDF via MS Office";
  }

  public String getID() {
    return "msoffice2pdf";
  }

}

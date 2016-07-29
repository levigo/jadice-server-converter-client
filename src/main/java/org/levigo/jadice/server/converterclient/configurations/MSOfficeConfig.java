package org.levigo.jadice.server.converterclient.configurations;

import java.net.URI;
import java.net.URISyntaxException;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.DynamicPipelineNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class MSOfficeConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) throws URISyntaxException {
    final DynamicPipelineNode msOfficePipeline = new DynamicPipelineNode();
    msOfficePipeline.setRuleset(new URI("resource:/dynamic-pipeline-rules/msoffice.xml"));
    
    job.attach(new StreamInputNode() //
      .appendSuccessor(msOfficePipeline) //
      .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "Office Documents to PDF via MS Office";
  }

  public String getID() {
    return "msoffice2pdf";
  }

}

package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.documentplatform.ReshapeNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class PDFReshapeConfig implements WorkflowConfiguration {

  public Job configureWorkflow(JobFactory jobFactory) throws Exception {
    final Job j = jobFactory.createJob();
    final ReshapeNode reshapeNode = new ReshapeNode();
    reshapeNode.setTargetMimeType("application/pdf");
    j.attach(new StreamInputNode()
      .appendSuccessor(reshapeNode)
      .appendSuccessor(new StreamOutputNode()));
    return j;
  }

  public String getDescription() {
    return "Convert to PDF via jadice document platform";
  }

  public String getID() {
    return "x2pdf (DOCP)";
  }

}

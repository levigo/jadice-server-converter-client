package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.msoffice.MSOutlookNode;
import com.levigo.jadice.server.nodes.DynamicPipelineNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;

public class OutlookConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) {
    job.attach(new StreamInputNode()//
        .appendSuccessor(new MSOutlookNode()) //
        .appendSuccessor(new DynamicPipelineNode()) //
        .appendSuccessor(new PDFMergeNode()) //
        .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "MS Outlook MSG to PDF via MS Outlook";
  }

  public String getID() {
    return "outlook2pdf";
  }

}

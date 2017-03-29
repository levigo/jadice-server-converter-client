package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFImageOptimizationNode;

public class OptimizePdfImagesConfig implements WorkflowConfiguration {

  @Override
  public void configureWorkflow(Job job) throws Exception {
    final PDFImageOptimizationNode imageOptimzeNode = new PDFImageOptimizationNode();
    imageOptimzeNode.setMaxResolution(300f);
    imageOptimzeNode.setJPEGQuality(0.75f);
    
    job.attach(new StreamInputNode()//
        .appendSuccessor(imageOptimzeNode)//
        .appendSuccessor(new StreamOutputNode()));
  }

  @Override
  public String getDescription() {
    return "Resample PDF images down to 300 dpi";
  }

  @Override
  public String getID() {
    return "resample-pdf (300dpi)";
  }
}

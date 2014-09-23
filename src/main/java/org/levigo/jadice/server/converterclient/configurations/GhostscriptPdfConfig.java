package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.ghostscript.GhostscriptNode;
import com.levigo.jadice.server.ghostscript.PDFOutputDevice;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class GhostscriptPdfConfig implements WorkflowConfiguration {

  public Job configureWorkflow(JobFactory jobFactory) throws Exception {
    final Job j = jobFactory.createJob();
    final GhostscriptNode gs = new GhostscriptNode();
    final PDFOutputDevice pdfDevice = new PDFOutputDevice();
    pdfDevice.setConvertCMYKImagesToRGB(true);
    gs.setOutputDevice(pdfDevice);
    j.attach(new StreamInputNode()//
    .appendSuccessor(gs)//
    .appendSuccessor(new StreamOutputNode()));
    return j;
  }

  public String getDescription() {
    return "Convert to PDF via GhostScript";
  }

  public String getID() {
    return "x2pdf (gs)";
  }
}

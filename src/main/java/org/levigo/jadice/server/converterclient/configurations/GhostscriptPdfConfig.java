package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.ghostscript.GhostscriptNode;
import com.levigo.jadice.server.ghostscript.PDFOutputDevice;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class GhostscriptPdfConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) throws Exception {
    final GhostscriptNode gs = new GhostscriptNode();
    final PDFOutputDevice pdfDevice = new PDFOutputDevice();
    pdfDevice.setConvertCMYKImagesToRGB(true);
    gs.setOutputDevice(pdfDevice);

    job.attach(new StreamInputNode()//
      .appendSuccessor(gs)//
      .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "Convert to PDF via GhostScript";
  }

  public String getID() {
    return "x2pdf (gs)";
  }
}

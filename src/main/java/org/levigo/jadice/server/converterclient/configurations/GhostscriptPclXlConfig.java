package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.ghostscript.GhostscriptNode;
import com.levigo.jadice.server.ghostscript.PXLOutputDevice;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class GhostscriptPclXlConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) {
    final GhostscriptNode gs = new GhostscriptNode();
    final PXLOutputDevice pclDevice = new PXLOutputDevice();
    pclDevice.setColor(true);
    gs.setOutputDevice(pclDevice);
    
    job.attach(new StreamInputNode()//
      .appendSuccessor(gs)//
      .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "PDF to PCL XL via GhostScript";
  }

  public String getID() {
    return "pdf2pclxl (gs)";
  }
}

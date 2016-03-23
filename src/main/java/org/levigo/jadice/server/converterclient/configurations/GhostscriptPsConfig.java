package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.ghostscript.GhostscriptNode;
import com.levigo.jadice.server.ghostscript.PSOutputDevice;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class GhostscriptPsConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) {
    final GhostscriptNode gs = new GhostscriptNode();
    final PSOutputDevice psDevice = new PSOutputDevice();
    psDevice.setEncapsulated(false);
    gs.setOutputDevice(psDevice);

    job.attach(new StreamInputNode()//
      .appendSuccessor(gs)//
      .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "PDF to Postscript via GhostScript";
  }

  public String getID() {
    return "pdf2ps (gs)";
  }
}

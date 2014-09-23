package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.ghostscript.GhostscriptNode;
import com.levigo.jadice.server.ghostscript.LaserJet4OutputDevice;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class GhostscriptPclConfig implements WorkflowConfiguration {

  public Job configureWorkflow(JobFactory jobFactory) throws Exception {
    final Job j = jobFactory.createJob();
    final GhostscriptNode gs = new GhostscriptNode();
    final LaserJet4OutputDevice pclDevice = new LaserJet4OutputDevice();
    pclDevice.setDuplex(true);
    gs.setOutputDevice(pclDevice);
    j.attach(new StreamInputNode()//
    .appendSuccessor(gs)//
    .appendSuccessor(new StreamOutputNode()));
    return j;
  }

  public String getDescription() {
    return "PDF to PCL via GhostScript";
  }

  public String getID() {
    return "pdf2pcl (gs)";
  }
}

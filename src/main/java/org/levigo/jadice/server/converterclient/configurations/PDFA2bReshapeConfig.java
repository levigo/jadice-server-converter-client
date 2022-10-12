package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.documentplatform.ReshapeNode;
import com.levigo.jadice.server.documentplatform.ReshapeNode.Conformance;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

  public class PDFA2bReshapeConfig implements WorkflowConfiguration {

    public void configureWorkflow(Job job) {
      final ReshapeNode reshapeNode = new ReshapeNode();
      reshapeNode.setTargetMimeType("application/pdf");
      reshapeNode.setConformance(Conformance.PDFA2b);

      job.attach(new StreamInputNode()
          .appendSuccessor(reshapeNode)
          .appendSuccessor(new StreamOutputNode()));
    }

    public String getDescription() {
      return "Convert to PDF-A/2b via jadice document platform";
    }

    public String getID() {
      return "x2pdf-A2b (DOCP)";
    }

  }

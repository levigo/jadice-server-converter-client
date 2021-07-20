package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.xrechnung.XRechnung2PdfNode;


public class XRechnung2PdfConfig implements WorkflowConfiguration {

    public void configureWorkflow(Job job) {
        job.attach(new StreamInputNode() //
                .appendSuccessor(new XRechnung2PdfNode()) ///
                .appendSuccessor(new StreamOutputNode()));
    }

    public String getDescription() {
        return "Convert XRechnung to PDF";
    }

    public String getID() {
        return "xrechnung2pdf";
    }

}

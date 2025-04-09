package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.documentplatform.StreamAnalysisNode;
import com.levigo.jadice.server.nodes.DynamicPipelineNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdf.PDFMergeNode;
import com.levigo.jadice.server.pdf.PortableCollectionDisassemblingNode;


public class ZUGFeRD2PDFConfig implements WorkflowConfiguration {

    public void configureWorkflow(Job job) {
        final StreamAnalysisNode streamAnalysisNode = new StreamAnalysisNode();
        final PortableCollectionDisassemblingNode disassemblingNode = new PortableCollectionDisassemblingNode();
        final DynamicPipelineNode dynamicPipelineNode = new DynamicPipelineNode();
        job.attach(new StreamInputNode() //
            .appendSuccessor(streamAnalysisNode) //
            .appendSuccessor(disassemblingNode) //
            .appendSuccessor(dynamicPipelineNode) //
            .appendSuccessor(new PDFMergeNode()) ///
            .appendSuccessor(new StreamOutputNode()) //
        );
    }

    public String getDescription() {
        return "Convert ZUGFeRD to PDF";
    }

    public String getID() {
        return "zugferd2pdf";
    }

}

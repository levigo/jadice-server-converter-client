package org.levigo.jadice.server.converterclient.configurations;

import java.util.EnumSet;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.pdfvalidation.PdfValidationNode;
import com.levigo.jadice.server.pdfvalidation.PdfValidationNode.ResultType;

public class PDFValidationConfig implements WorkflowConfiguration {

  public void configureWorkflow(Job job) {
    final PdfValidationNode pdfValidationNode = new PdfValidationNode();
    pdfValidationNode.setFlavourId("2b");
    pdfValidationNode.setDoValidationTask(true); // perform PDF/A validation
    pdfValidationNode.setDoFixMetadataTask(false); // do not fix metadata since it may take some additional time
    pdfValidationNode.setFeatureExtractionConfigIndex(1); // perform the minimal set of feature extractions since
    // performance is more important for this workflow
    pdfValidationNode.setFixupFailureAction(PdfValidationNode.FixupFailureAction.WARN);
    pdfValidationNode.setMaxFails(-1); // how many issues are allowed -1 = indefinite
    pdfValidationNode.setRecordPasses(false); // do not list the checked steps since it could be a very long list
    pdfValidationNode.setTargetResults(EnumSet.of(ResultType.REPORT_XML)); // only return the Report

    job.attach(new StreamInputNode()
        .appendSuccessor(pdfValidationNode)
        .appendSuccessor(new StreamOutputNode()));
  }

  public String getDescription() {
    return "Validate PDF with veraPDF";
  }

  public String getID() {
    return "validatePDF (2b)";
  }

}

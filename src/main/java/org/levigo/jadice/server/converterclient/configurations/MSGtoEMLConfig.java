package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.javamail.TNEFNode;
import com.levigo.jadice.server.javamail.TNEFNode.InputFormat;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class MSGtoEMLConfig implements WorkflowConfiguration {

  public Job configureWorkflow(JobFactory jobFactory) throws Exception {
    TNEFNode tnefNode = new TNEFNode();
    tnefNode.setInputFormat(InputFormat.MSG);


    Job j = jobFactory.createJob();

    j.attach(new StreamInputNode() //
    .appendSuccessor(tnefNode) //
    .appendSuccessor(new StreamOutputNode()));

    return j;
  }

  public String getDescription() {
    return "MS Outlook MSG to EML";
  }

  public String getID() {
    return "msg2eml";
  }

}

package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.documentplatform.ReshapeNode;
import com.levigo.jadice.server.documentplatform.ReshapeNode.OutputMode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class TiffReshapeConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
		
		ReshapeNode tiffShaper = new ReshapeNode();
		tiffShaper.setTargetMimeType("image/tiff");
		tiffShaper.setOutputMode(OutputMode.JOINED);
		
		j.attach(new StreamInputNode() //
				.appendSuccessor(tiffShaper) //
				.appendSuccessor(new StreamOutputNode()));
		return j;
	}

	public String getDescription() {
		return "Convert to TIFF via jadice document platform";
	}

	public String getID() {
		return "x2tiff (DOCP)";
	}

}

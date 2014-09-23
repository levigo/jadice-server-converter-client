package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.documentplatform.JadiceToTiffNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

@SuppressWarnings("deprecation")
public class TiffReshapeConfig implements WorkflowConfiguration {

	public Job configureWorkflow(JobFactory jobFactory) throws Exception {
		Job j = jobFactory.createJob();
//		JadiceShaperNode tiffShaper = new JadiceShaperNode();
//		tiffShaper.setOutputMode(OutputMode.JOINED);
//		tiffShaper.getSettings(TiffConvertConfiguration.class).setRepackingEnabled(false);
//		tiffShaper.getSettings(TiffConvertConfiguration.class).setRenderAnnotations(true);
//		tiffShaper.getSettings(TiffConvertConfiguration.class).setCompression(Compression.AUTO);
//		tiffShaper.getSettings(TiffConvertConfiguration.class).setFontRenderingMode(FontRenderingMode.SHAPE_RENDERING);
		j.attach(new StreamInputNode() //
				.appendSuccessor(new JadiceToTiffNode()) //
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

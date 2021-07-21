package org.levigo.jadice.server.converterclient.configurations;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.imagemagick.ImageMagickConvertNode;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;

public class IMHeic2PngConfig implements WorkflowConfiguration  {

    public void configureWorkflow(Job job) {
        job.attach(new StreamInputNode() //
                .appendSuccessor(new ImageMagickConvertNode()) ///
                .appendSuccessor(new StreamOutputNode()));
    }

    public String getDescription() {
        return "Convert heic to png";
    }

    public String getID() {
        return "heic2png";
    }

}

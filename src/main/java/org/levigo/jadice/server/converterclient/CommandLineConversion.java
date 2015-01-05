package org.levigo.jadice.server.converterclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;

public class CommandLineConversion {
	
	final WorkflowConfiguration config;
	
	final File inFile;
	
	final String outFileName;
	
	final String serverLocation;
	
	
	public CommandLineConversion(String configID, String inFileName, String outFileName, String serverLocation) throws Exception {
		config = JobCardFactory.getInstance().getConfiguration(configID);
		if (config == null) {
			throw new IllegalArgumentException("Configuration \"" + configID + "\" unknown");
		}
		
		inFile = new File(inFileName);
	
		if (!inFile.isFile() || !inFile.canRead()) {
			String msg = "File \"" + inFileName + "\" is not readable";
			throw new IOException(msg);
		}
		
		this.serverLocation = serverLocation;
		this.outFileName = outFileName;
	}
	
	public List<File> runConversion() throws Exception {
		List<File> result = new ArrayList<File>();
		JobCard jobCard = JobCardFactory.getInstance().createAndSubmitJobCard(inFile, serverLocation, config, Collections.emptySet());
		
		jobCard.job.waitForTermination(-1);
		
		while (!jobCard.isResultsCompleted()) {
			Thread.sleep(100);
		}
		
		if (jobCard.getResults().size() == 1) {
			File from = jobCard.getResults().get(0);
			File to = new File(this.outFileName);
			copyFile(from, to);
			result.add(to);
		} else {
			int count = 0;
			for (File from : jobCard.getResults()) {
				File to = new File(injectCount(this.outFileName, count++));
				copyFile(from, to);
				result.add(to);
			}
		}
		
		JobCardScheduler.getInstance().shutdown();
		
		return result;
	
	}
	
  public static void copyFile(File from, File to) throws IOException {
    final FileInputStream fis = new FileInputStream(from);
    try {
      final FileOutputStream fos = new FileOutputStream(to);
      try {
        final FileChannel inChannel = fis.getChannel();
        final FileChannel outChannel = fos.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
      } finally {
        fos.close();
      }
    } finally {
      fis.close();
    }
  }
		
		private static String injectCount(String origFilename, int count) {
		if (origFilename == null)
			return null;

		String ext = "";
		// Consider path separators
		// as files have not always extension
		// but paths can have a "." in their name
		int idxPathWin = origFilename.lastIndexOf("/");
		int idxPathUnix = origFilename.lastIndexOf("\\");
		int idx = origFilename.lastIndexOf(".");
		if (idx != -1 && idx > idxPathWin && idx > idxPathUnix) {
			origFilename = origFilename.substring(0, idx);
			ext = origFilename.substring(idx);
		}

		return origFilename + count + ext;
	}

}

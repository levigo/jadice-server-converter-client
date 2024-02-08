package org.levigo.jadice.server.converterclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;

public class JS5TimedLoadTest {

  private long testDurationMS = TimeUnit.MINUTES.toMillis(15);

  private String templateID = "x2tiff (DOCP)";
  private File testdataFolder = new File(System.getProperty("user.dir"), "/testdata/perf-test/image");

  // private String templateID = "mail2pdf (lo)";
  // private File testdataFolder = new File(System.getProperty("user.dir"),
  // "/testdata/perf-test/email");

  private int targetQueueSize = 40;

  private JobCardScheduler scheduler = JobCardScheduler.getInstance();

  private long sleepTimeBetweenCheckMS = 1000;

  private List<File> testdataImageFiles = new ArrayList<>();
  private int currentTestdataIndex = 0;

  private List<JobCard> createdJobCards = new ArrayList<>();
  private int currentItemCounter = 0;

  private String serverLocation = "tcp://localhost:61616";
  private WorkflowConfiguration config;

  @Disabled // TODO: Enable on your local machine
  @Test
  public void performLoadTest() throws Exception {
    init();

    long totalDurationMS = System.currentTimeMillis();
    config = JobCardFactory.getInstance().getConfiguration(templateID);
    if (config == null) {
      throw new IllegalArgumentException("Configuration \"" + templateID + "\" unknown");
    }

    Map<String, Object> info = new HashMap<>();
    info.put("desiredRuntime [sec]", (testDurationMS / 1000));
    info.put("templateID", templateID);
    info.put("targetQueueSize", targetQueueSize);
    info.put("startTime", new Date(System.currentTimeMillis()));

    System.out.println("Performing load test...\n" + info);

    doPerformTest();

    totalDurationMS = System.currentTimeMillis() - totalDurationMS;

    // Add some statistics
    info.put("createdItemCount", currentItemCounter);
    info.put("createdJobCount", createdJobCards.size());

    double timePerJob = Double.valueOf(totalDurationMS).doubleValue()
        / Double.valueOf(createdJobCards.size()).doubleValue();
    double timePerItem = Double.valueOf(totalDurationMS).doubleValue()
        / Double.valueOf(currentItemCounter).doubleValue();
    double jobsPerMinute = Double.valueOf(60000) / timePerJob;
    double itemsPerMinute = Double.valueOf(60000) / timePerItem;
    double itemsPerHour = itemsPerMinute * 60;

    info.put("timePerItem", timePerItem);
    info.put("itemsPerMinute", itemsPerMinute);
    info.put("itemsPerHour", itemsPerHour);
    info.put("timePerJob", timePerJob);
    info.put("jobsPerMinute", jobsPerMinute);

    // Log finished message
    StringBuilder sb = new StringBuilder();
    for (String key : info.keySet()) {
      if (sb.length() > 0) {
        sb.append(System.lineSeparator());
      }
      sb.append(key);
      sb.append("=");
      sb.append(info.get(key));
    }

    String msg = String.format("Finished load test:\n===========================\n%s\n===========================",
        sb.toString());

    System.out.println(msg);

    System.out.println("Test ended");
  }

  private void init() {
    System.out.println("Reading testdata folder...");
    // Read all file objects from folder so we can easily choose later on via rotating index
    testdataImageFiles.clear();
    if (testdataFolder.exists()) {
      File[] sub = testdataFolder.listFiles();
      if (null != sub) {
        for (File s : sub) {
          testdataImageFiles.add(s);
        }
      }
    }
  }

  @Before
  private void beforeEachTest() {
    currentItemCounter = 0;
    createdJobCards.clear();
    currentTestdataIndex = 0;
  }

  private void doPerformTest() throws Exception {
    boolean shallRun = true;
    long start = System.currentTimeMillis();
    long end = start + testDurationMS;

    System.out.println("Running performance test. Will run until: " + new Date(end));

    while (shallRun) {
      int currentQueueSize = scheduler.getCurrentQueueSize();

      if (currentQueueSize < targetQueueSize) {
        System.out.println("Creating new jobs to fill up queue...");

        while (currentQueueSize < targetQueueSize) {

          System.out.println("Creating next job");
          JobCard jobCard = createNextJob();
          createdJobCards.add(jobCard);

          currentItemCounter++;

          currentQueueSize = scheduler.getCurrentQueueSize();
        }
      }

      if (System.currentTimeMillis() > end) {
        shallRun = false;
      } else {
        Thread.sleep(sleepTimeBetweenCheckMS);
        System.out.println(String.format(
            "Running... Created job count: %s / created item count %s / runtime left %s min", createdJobCards.size(),
            currentItemCounter, Duration.ofMillis(end - System.currentTimeMillis()).toMinutes()));
      }
    }

    System.out.println("Loop finished");
  }

  private JobCard createNextJob() throws Exception {

    if (currentTestdataIndex > testdataImageFiles.size() - 2) {
      currentTestdataIndex = 0;
    } else {
      currentTestdataIndex++;
    }

    File chosenFile = testdataImageFiles.get(currentTestdataIndex);

    JobCard jobCard = JobCardFactory.getInstance().createAndSubmitJobCard(chosenFile, serverLocation, config,
        Collections.emptySet());

    return jobCard;
  }

  private void processResult(JobCard jobCard) throws IOException, InterruptedException {
    List<File> result = new ArrayList<File>();

    jobCard.job.waitForTermination(-1, TimeUnit.SECONDS);

    while (!jobCard.isResultsCompleted()) {
      Thread.sleep(100);
    }

    for (File from : jobCard.getResults()) {
      String outFilename = "./target/out/" + /* inFile.getName() + */ "-out-" + UUID.randomUUID().toString() + "-"
          + from.getName();
      File outFile = new File(System.getProperty("user.dir"), outFilename);

      copyFile(from, outFile);
      result.add(outFile);
    }
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
}

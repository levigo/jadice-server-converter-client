package org.levigo.jadice.server.converterclient;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobCardScheduler {

  private final static JobCardScheduler INSTANCE = new JobCardScheduler();

  private static final class MyShutdownHook extends Thread {

    public MyShutdownHook() {
      super("ShutdownHook for JobCardScheduler");
    }

    @Override
    public void run() {
      getInstance().executor.shutdown();
      getInstance().executor.shutdownNow();
    }
  }

  public static JobCardScheduler getInstance() {
    return INSTANCE;
  }

  private JobCardScheduler() {
    Runtime.getRuntime().addShutdownHook(new MyShutdownHook());
    Preferences.concurrentJobsProperty().addListener((obj, oldValue, newValue) -> {
      executor.setCorePoolSize(newValue.intValue());
    });
    executor.setCorePoolSize(Preferences.concurrentJobsProperty().getValue());
  }

  private ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<Runnable>());

  public void submit(JobCard card) {
    executor.submit(card);
  }

  public void shutdown() {
    this.executor.shutdown();
  }

  public int getCurrentQueueSize() {
    return executor.getQueue().size();
  }
}

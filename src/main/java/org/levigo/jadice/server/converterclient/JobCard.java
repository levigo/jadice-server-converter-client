package org.levigo.jadice.server.converterclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.log4j.Logger;
import org.levigo.jadice.server.converterclient.LogMessage.Type;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;
import org.levigo.jadice.server.converterclient.util.FilenameGenerator;

import com.levigo.jadice.filetype.Analyzer;
import com.levigo.jadice.filetype.UncloseableSeekableInputStreamWrapper;
import com.levigo.jadice.filetype.database.ExtensionAction;
import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.Job.State;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.JobExecutionException;
import com.levigo.jadice.server.JobListener;
import com.levigo.jadice.server.Node;
import com.levigo.jadice.server.internal.JobInternal;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamListener;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.shared.types.Stream;
import com.levigo.jadice.server.shared.types.StreamDescriptor;
import com.levigo.jadice.server.util.Util;

public class JobCard implements Runnable, JobListener, StreamListener {
  
  private static final Logger LOGGER = Logger.getLogger(JobCard.class);

  private final AtomicInteger nodeCount = new AtomicInteger(0);

  @Override
  public void finished() {
    if (nodeCount.decrementAndGet() == 0) {
      resultsCompleted();
    }
  }

  @Override
  public void streamAvailable(final Stream stream) {
    addResult(stream);
  }

  private boolean submitted = false;

  @Override
  public void stateChanged(Job job, State oldState, State newState) {
    addLogMessage(new LogMessage(new Date(), Type.STATE, null, null, oldState + " -> " + newState, null));
    jobStateProperty.setValue(newState);
    serverInstanceNameProperty.setValue(job.getServerInstanceName());
    if (newState == State.STARTED && !submitted) {
      try {
        for (File f : files) {
          submitFile(f);
        }
        submitted = true;
        sin.complete();
      } catch (FileNotFoundException e) {
        abortJob();
      } catch (IOException e) {
        abortJob();
      }
    }
  }

  private void submitFile(File file) throws IOException, FileNotFoundException {
    if (file.isDirectory() && file.listFiles() != null) {
      for (File child : file.listFiles()) {
        submitFile(child);
      }
    }
    StreamDescriptor sd = new StreamDescriptor();
    sd.setFileName(file.getName());
    sin.addStream(new FileInputStream(file), sd);
  }

  @Override
  public void errorOccurred(Job job, Node node, String messageId, String message, Throwable cause) {
    addLogMessage(new LogMessage(new Date(), Type.ERROR, node, messageId, message, cause));
  }

  @Override
  public void executionFailed(Job job, Node node, String messageId, String reason, Throwable cause) {
    if (cause != null)
      cause.printStackTrace();
    addLogMessage(new LogMessage(new Date(), Type.FATAL, node, messageId, reason, cause));
  }

  @Override
  public void warningOccurred(Job job, Node node, String messageId, String message, Throwable cause) {
    addLogMessage(new LogMessage(new Date(), Type.WARNING, node, messageId, message, cause));
  }

  @Override
  public void subPipelineCreated(Job job, Node parent, Set<? extends Node> created) {
    Date timestamp = new Date();
    for (Node newNode : created) {
      addLogMessage(new LogMessage(timestamp, Type.SUB_NODE_CREATED, parent, null, newNode.getClass().getName(), null));
    }

  }
  
  public final Job job;
  
  public final ObservableList<File> files;
  
  public final WorkflowConfiguration config;
  
  public final ObjectProperty<State> jobStateProperty;

  private final StreamInputNode sin;

  private final Collection<StreamOutputNode> son;

  private final AtomicInteger copiesInProgress = new AtomicInteger(0);

  private boolean resultsFinished = false;

  private final ObservableList<File> results = FXCollections.observableArrayList();
  
  private final AtomicInteger resultCount = new AtomicInteger(0);
  
  public final IntegerProperty warningCount = new SimpleIntegerProperty(0);
  
  public final IntegerProperty errorCount = new SimpleIntegerProperty(0);
  
  public final StringProperty serverInstanceNameProperty;

  private final ObservableList<LogMessage> logMessages = FXCollections.observableArrayList();

  public JobCard(Job job, StreamInputNode sin, Collection<StreamOutputNode> son, File file, WorkflowConfiguration config) {
    this(job, sin, son, Collections.singletonList(file), config);
  }

  public JobCard(Job job, StreamInputNode sin, Collection<StreamOutputNode> son, List<File> files,
      WorkflowConfiguration config) {
    this.job = job;
    this.files = FXCollections.unmodifiableObservableList(FXCollections.observableList(files));
    this.sin = sin;
    this.son = Collections.unmodifiableCollection(son);
    this.config = config;
    this.jobStateProperty = new SimpleObjectProperty<>(job.getState());
    this.serverInstanceNameProperty = new SimpleStringProperty(job.getServerInstanceName());
  }

  public void addResult(final Stream stream) {
    copiesInProgress.incrementAndGet();
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Analyzer al = Analyzer.getInstance("/magic-all.xml");
          UncloseableSeekableInputStreamWrapper usis = new UncloseableSeekableInputStreamWrapper(stream.getInputStream());
          Map<String, Object> alResults;
          try {
            usis.lockClose();
            alResults = al.analyze(usis);
          } finally {
            usis.unlockClose();
          }

          final Object o = alResults.get(ExtensionAction.KEY);
          final String ext = (o != null) ? o.toString() : Preferences.defaultExtensionProperty().getValue();

          final String filename = FilenameGenerator.generateFilename(job, files.isEmpty() ? null : files.get(0), resultCount.incrementAndGet(), ext);
          final File file = new File(Preferences.resultFolderProperty().getValue(), filename);
          final FileOutputStream fos = new FileOutputStream(file);
          
          usis.seek(0);
          Util.copyAndClose(usis, fos);
          LOGGER.info("Copied result to " + file.getCanonicalPath());
          results.add(file);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          copiesInProgress.decrementAndGet();
          try {
            stream.getInputStream().close();
          } catch (IOException e) {
            LOGGER.warn("Error when closing stream", e);
          }
        }

      }
    }).start();
  }

  public ObservableList<File> getResults() {
    return FXCollections.unmodifiableObservableList(results);
  }

  public void addLogMessage(LogMessage logMessage) {
    logMessages.add(logMessage);
    switch (logMessage.type){
      case ERROR :
        errorCount.set(errorCount.get() + 1);
        break;
      case WARNING:
        warningCount.set(warningCount.get() + 1);
        break;

      default :
        break;
    }
    for (LogMessageListener l : this.logMessageListeners)
      l.logMessageAdded(logMessage, this);
  }

  public ObservableList<LogMessage> getLogMessages() {
    return FXCollections.unmodifiableObservableList(logMessages);
  }
  
  public int getWarningCount() {
    return warningCount.getValue();
  }
  
  public int getErrorCount() {
    return errorCount.getValue();
  }
  
  private final List<LogMessageListener> logMessageListeners = new ArrayList<>();

  public void addLogMessageListener(LogMessageListener logMessageListener) {
    this.logMessageListeners.add(logMessageListener);
  }

  public void abortJob() {
    if (job.getState().isTerminalState())
      return;

    try {
      job.abort();
    } catch (JobExecutionException jee) {
      // Server might be down, so give a damn about this exception
      try {
        ((JobInternal) job).setState(State.ABORTED);
        stateChanged(job,  jobStateProperty.get(), State.ABORTED);
      } catch (Throwable e2) {
        LOGGER.error("Error when manually setting job state to abort", e2);
      }
    } catch (Throwable e) {
      LOGGER.error("Error on job abort", e);
    }
    // Avoid GUI glitches
    jobStateProperty.setValue(State.ABORTED);
  }

  private void resultsCompleted() {
    resultsFinished = true;
  }

  public boolean isResultsCompleted() {
    return resultsFinished && copiesInProgress.get() == 0;

  }

  @Override
  public void run() {
    try {
      job.addJobListener(this);
      for (StreamOutputNode soNode : son) {
        nodeCount.incrementAndGet();
        soNode.addStreamResultListener(this);
      }
      job.submit();
      job.waitForTermination(-1);
    } catch (JobException e) {
      LOGGER.error("Job submit failed", e);
    }
  }

}
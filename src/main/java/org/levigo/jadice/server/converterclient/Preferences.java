package org.levigo.jadice.server.converterclient;

import java.io.File;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

import org.apache.log4j.Logger;
import org.levigo.jadice.server.converterclient.util.FilenameGenerator;

import com.levigo.jadice.server.client.jms.JMSJobFactory;

public class Preferences {
  
  private static final Logger LOGGER = Logger.getLogger(Preferences.class);
	
	public static interface Defaults {
	  final String RECENT_SERVERS = "tcp://localhost:61616";
	  final String RECENT_JMX_URLS = "localhost:61619";
	  final String RESULT_EXTENSION = "dat";
	  final String RESULT_FILENAME_PATTERN = FilenameGenerator.DEFAULT_PATTERN;
	  final File RESULT_FOLDER = new File(System.getProperty("java.io.tmpdir"));
	  final int CONCURRENT_JOBS = 4;
	  
	  final String JMS_REQUEST_QUEUE_NAME = JMSJobFactory.DEFAULT_QUEUE_NAME;
	  final String JMS_LOG_TOPIC_NAME = "JS.LOG";
	  final String JMS_USER_NAME = null;
	  final String JMS_PASSWORD = null;
	  final boolean JMS_JOBFACTORY_CACHING = true;
    final int JMS_JOB_PRIORITY = JMSJobFactory.DEFAULT_PRIORITY; 
	}
	
	private static interface Keys {
	  final String RECENT_SERVERS = "servers";
	  final String RECENT_JMX_URLS = "jmx";
	  final String RESULT_EXTENSION = "defaultExtension";
	  final String RESULT_FILENAME_PATTERN = "resultFilenamePattern";
	  final String RESULT_FOLDER = "resultFolder";
	  final String CONCURRENT_JOBS = "concurrentJobs";
    
    final String JMS_REQUEST_QUEUE_NAME = "jms.requestQueue";
    final String JMS_LOG_TOPIC_NAME = "jms.logTopic";
    final String JMS_USER_NAME = "jms.username";
    final String JMS_PASSWORD = "jms.password";
    final String JMS_JOBFACTORY_CACHING = "jms.enableJobfactoryCaching";
    final String JMS_JOB_PRIORITY = "jms.jobPriority";
    
	}

  private static java.util.prefs.Preferences PREF = java.util.prefs.Preferences.userNodeForPackage(Preferences.class);
  
	private static final String STRING_SEPARATOR = "##";
	
	private static ListProperty<String> recentServersProperty;
	
	private static ListProperty<String> recentJmxUrlsProperty;
	
	private static StringProperty defaultExtensionProperty;
	
	private static ObjectProperty<File> resultFolderProperty;
	private static StringProperty resultFilenamePatternProperty;
	private static StringProperty jmsRequestQueueNameProperty;
	private static StringProperty jmsLogTopicNameProperty;
	private static StringProperty jmsUsernameProperty;
	private static StringProperty jmsPasswordProperty;
	private static BooleanProperty cacheJmsJobFactoryProperty;
	private static IntegerProperty jmsJobPriority;
	private static IntegerProperty concurrentJobsProperty;

	
  public static ListProperty<String> recentServersProperty() {
    if (recentServersProperty == null) {
      final String[] split = PREF.get(Keys.RECENT_SERVERS, Defaults.RECENT_SERVERS).split(STRING_SEPARATOR);
      recentServersProperty = new SimpleListProperty<String>(FXCollections.observableArrayList(split));
      recentServersProperty.addListener((ListChangeListener<String>) evt -> {
        saveRecentServers(recentServersProperty.getValue());
      });
    }
    return recentServersProperty;
  }
	
	private static void saveRecentServers(List<String> servers) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (String s : servers) {
			if (isFirst)
				isFirst = false;
			else 
				sb.append(STRING_SEPARATOR);
			
			sb.append(s);
		}
		putNullSafe(Keys.RECENT_SERVERS, sb.toString());
	}
	
  public static ListProperty<String> recentJmxUrlsProperty() {
    if (recentJmxUrlsProperty == null) {
      final String[] split = PREF.get(Keys.RECENT_JMX_URLS, Defaults.RECENT_JMX_URLS).split(STRING_SEPARATOR);
      recentJmxUrlsProperty = new SimpleListProperty<String>(FXCollections.observableArrayList(split));
      recentJmxUrlsProperty.addListener((ListChangeListener<String>) evt -> {
        saveRecentJmxUrls(recentJmxUrlsProperty.getValue());
      });
    }
    return recentJmxUrlsProperty;
  }
	
	public static void saveRecentJmxUrls(List<String> urls) {
	  StringBuilder sb = new StringBuilder();
	  boolean isFirst = true;
	  for (String s : urls) {
	    if (isFirst)
	      isFirst = false;
	    else 
	      sb.append(STRING_SEPARATOR);
	    
	    sb.append(s);
	  }
	  putNullSafe(Keys.RECENT_JMX_URLS, sb.toString());
	}
	
	public static StringProperty defaultExtensionProperty() {
	  if (defaultExtensionProperty == null) {
	    defaultExtensionProperty = new SimpleStringProperty(PREF.get(Keys.RESULT_EXTENSION, Defaults.RESULT_EXTENSION));
	    defaultExtensionProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.RESULT_EXTENSION, newValue);
      });
	  }
	  return defaultExtensionProperty;
	}
	
	public static ObjectProperty<File> resultFolderProperty() {
	  if (resultFolderProperty == null) {
	    String dir = PREF.get(Keys.RESULT_FOLDER, null);
	    resultFolderProperty = new SimpleObjectProperty<>((dir == null || dir.isEmpty()) ? Defaults.RESULT_FOLDER : new File(dir));
	    resultFolderProperty.addListener((observable, oldValue, newValue) ->
	    {
	      putNullSafe(Keys.RESULT_FOLDER, newValue == null ? null : newValue.getAbsolutePath());
	    });
	  }
	  return resultFolderProperty;
	}
	
	public static StringProperty resultFilenamePatternProperty() {
	  if (resultFilenamePatternProperty == null) {
	    resultFilenamePatternProperty = new SimpleStringProperty(PREF.get(Keys.RESULT_FILENAME_PATTERN, Defaults.RESULT_FILENAME_PATTERN));
	    resultFilenamePatternProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.RESULT_FILENAME_PATTERN, newValue);
      });
	  }
	  return resultFilenamePatternProperty;
	}
	
  public static IntegerProperty concurrentJobsProperty() {
    if (concurrentJobsProperty == null) {
      concurrentJobsProperty = new SimpleIntegerProperty(PREF.getInt(Keys.CONCURRENT_JOBS, Defaults.CONCURRENT_JOBS));
      concurrentJobsProperty.addListener((observable, oldvalue, newValue) -> 
      {
        PREF.putInt(Keys.CONCURRENT_JOBS, newValue.intValue());
      });
    }
    return concurrentJobsProperty;
  }

	
  public static StringProperty jmsRequestQueueNameProperty() {
    if (jmsRequestQueueNameProperty == null) {
      jmsRequestQueueNameProperty = new SimpleStringProperty(PREF.get(Keys.JMS_REQUEST_QUEUE_NAME, Defaults.JMS_REQUEST_QUEUE_NAME));
      jmsRequestQueueNameProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.JMS_REQUEST_QUEUE_NAME, newValue);
      });
    }
    return jmsRequestQueueNameProperty;
	}
  
  public static StringProperty jmsLogTopicNameProperty() {
    if (jmsLogTopicNameProperty == null) {
      jmsLogTopicNameProperty = new SimpleStringProperty(PREF.get(Keys.JMS_LOG_TOPIC_NAME, Defaults.JMS_LOG_TOPIC_NAME));
      jmsLogTopicNameProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.JMS_LOG_TOPIC_NAME, newValue);
      });
    }
    return jmsLogTopicNameProperty;
  }
  
	public static StringProperty jmsUsernameProperty() {
	  if (jmsUsernameProperty == null) {
	    jmsUsernameProperty = new SimpleStringProperty(PREF.get(Keys.JMS_USER_NAME, Defaults.JMS_USER_NAME));
	    jmsUsernameProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.JMS_USER_NAME, newValue);
      });
	  }
	  return jmsUsernameProperty;
	}
	
	public static StringProperty jmsPasswordProperty() {
	  if (jmsPasswordProperty == null) {
	    jmsPasswordProperty = new SimpleStringProperty(PREF.get(Keys.JMS_PASSWORD, Defaults.JMS_PASSWORD));
	    jmsPasswordProperty.addListener((observable, oldValue, newValue) ->
      {
        putNullSafe(Keys.JMS_PASSWORD, newValue);
      });
	  }
	  return jmsPasswordProperty;
	}
	
  public static BooleanProperty cacheJmsJobFactoryProperty() {
    if (cacheJmsJobFactoryProperty == null) {
      cacheJmsJobFactoryProperty = new SimpleBooleanProperty(PREF.getBoolean(Keys.JMS_JOBFACTORY_CACHING ,Defaults.JMS_JOBFACTORY_CACHING));
      cacheJmsJobFactoryProperty.addListener((observable, oldValue, newValue) ->
      {
        PREF.putBoolean(Keys.JMS_JOBFACTORY_CACHING, newValue);
      });
    }
    return cacheJmsJobFactoryProperty;
  }
  
  public static IntegerProperty jmsJobPriority() {
    if (jmsJobPriority == null) {
      jmsJobPriority = new SimpleIntegerProperty(PREF.getInt(Keys.JMS_JOB_PRIORITY, Defaults.JMS_JOB_PRIORITY));
      jmsJobPriority.addListener((oberservable, oldValue, newValue) -> 
      {
        PREF.putInt(Keys.JMS_JOB_PRIORITY, newValue.intValue());
      });
    }
    return jmsJobPriority;
  }
  
  public static void restoreDefaults() {
    recentServersProperty().setAll(Defaults.RECENT_SERVERS);
    recentJmxUrlsProperty().setAll(Defaults.RECENT_JMX_URLS);
    defaultExtensionProperty().set(Defaults.RESULT_EXTENSION);
    concurrentJobsProperty().set(Defaults.CONCURRENT_JOBS);
    resultFilenamePatternProperty().set(Defaults.RESULT_FILENAME_PATTERN);
    resultFolderProperty().set(Defaults.RESULT_FOLDER);
    jmsUsernameProperty().set(Defaults.JMS_USER_NAME);
    jmsPasswordProperty().set(Defaults.JMS_PASSWORD);
    jmsRequestQueueNameProperty().set(Defaults.JMS_REQUEST_QUEUE_NAME);
    jmsLogTopicNameProperty().set(Defaults.JMS_LOG_TOPIC_NAME);
    cacheJmsJobFactoryProperty().set(Defaults.JMS_JOBFACTORY_CACHING);
    
  }

  private static void putNullSafe(String key, String value) {
    if (key == null)
      throw new IllegalArgumentException("key must not be null");
    
    if (value == null) {
      LOGGER.debug("Removing prefences for " + key);
      PREF.remove(key);
    } else {
      LOGGER.debug("Storing preferences for " + key + ": " + value);
      PREF.put(key, value);
    }
  }
}
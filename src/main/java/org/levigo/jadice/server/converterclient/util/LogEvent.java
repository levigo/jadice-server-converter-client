package org.levigo.jadice.server.converterclient.util;

import java.util.Date;

import org.apache.log4j.Level;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class LogEvent {
  
  private final ReadOnlyProperty<Date> timestampProperty;
  
  private final ReadOnlyProperty<Level> levelProperty;

  private final ReadOnlyStringProperty loggerNameProperty;

  private final ReadOnlyStringProperty threadNameProperty;

  private final ReadOnlyStringProperty ndcProperty;

  private final ReadOnlyStringProperty messageProperty;

  private final ReadOnlyStringProperty stacktraceProperty;

  
  public LogEvent(Date timestamp, Level level, String loggerName, String threadName, String ndc, String message, String stacktrace) {
    timestampProperty = new SimpleObjectProperty<>(timestamp);
    levelProperty = new SimpleObjectProperty<>(level);
    loggerNameProperty = new SimpleStringProperty(loggerName);
    threadNameProperty = new SimpleStringProperty(threadName);
    ndcProperty = new SimpleStringProperty(ndc);
    messageProperty = new SimpleStringProperty(message);
    stacktraceProperty = new SimpleStringProperty(stacktrace);
  }
  
  public ReadOnlyProperty<Date> timestampProperty() {
    return timestampProperty;
    
  }
  
  public ReadOnlyProperty<Level> levelProperty() {
    return levelProperty;
  }
  
  public ReadOnlyStringProperty ndcProperty() {
    return ndcProperty;
  }
  
  public ReadOnlyStringProperty loggerNameProperty() {
    return loggerNameProperty;
  }
  
  public ReadOnlyStringProperty threadNameProperty() {
    return threadNameProperty;
  }
  public ReadOnlyStringProperty messageProperty() {
    return messageProperty;
  }
  
  public ReadOnlyStringProperty stacktraceProperty() {
    return stacktraceProperty;
  }
}

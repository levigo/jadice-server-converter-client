package org.levigo.jadice.server.converterclient.util;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class Log4JForwarder extends WriterAppender {

  public static interface LogHandler {
    void appendLogEntry(String s);
  }

  private static Log4JForwarder INSTANCE;

  public static Log4JForwarder getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Log4JForwarder();
    }
    return INSTANCE;
  }

  public Log4JForwarder() {
    INSTANCE = this;
  }

  private LogHandler handler;

  public void setLogHandler(LogHandler handler) {
    this.handler = handler;
  }

  public LogHandler getLogHandler() {
    return handler;
  }

  /**
   * Format and then append the loggingEvent to the stored TextArea.
   *
   * @param loggingEvent
   */
  @Override
  public void append(final LoggingEvent loggingEvent) {
    if (handler == null) {
      return;
    }
    final String message = this.layout.format(loggingEvent);
    handler.appendLogEntry(message);
    
    if (!layout.ignoresThrowable() || loggingEvent.getThrowableStrRep() == null) {
      return;
    }
    for (String s : loggingEvent.getThrowableStrRep()) {
      handler.appendLogEntry(s);
      handler.appendLogEntry(Layout.LINE_SEP);
    }
  }
}

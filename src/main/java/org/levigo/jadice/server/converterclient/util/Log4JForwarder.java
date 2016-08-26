package org.levigo.jadice.server.converterclient.util;

import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class Log4JForwarder extends WriterAppender {
  
  public static interface LogHandler {
    void appendLogEntry(String s);
  }

  private final static CopyOnWriteArraySet<LogHandler> HANDLERS = new CopyOnWriteArraySet<>();

  /**
   * Format and then append the loggingEvent to the stored TextArea.
   *
   * @param loggingEvent
   */
  @Override
  public void append(final LoggingEvent loggingEvent) {
    if (HANDLERS.isEmpty()) {
      return;
    }
    final String message = this.layout.format(loggingEvent);
    HANDLERS.stream().forEach(h -> h.appendLogEntry(message));
    
    if (!layout.ignoresThrowable() || loggingEvent.getThrowableStrRep() == null) {
      return;
    }
    for (String s : loggingEvent.getThrowableStrRep()) {
      HANDLERS.stream().forEach(h-> {
          h.appendLogEntry(s);
          h.appendLogEntry(Layout.LINE_SEP);
      });
    }
  }

  public static void registerLogHandler(LogHandler handler) {
    HANDLERS.add(handler);
  }
}

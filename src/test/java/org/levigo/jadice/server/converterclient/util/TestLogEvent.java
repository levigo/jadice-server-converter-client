package org.levigo.jadice.server.converterclient.util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

public class TestLogEvent {

  /**
   * The Parser under test
   */
  private static final LogEventParser PARSER = LogEventParser.getInstance();

  private static final String SIMPLE_EVENT_RESOURCE = "/log-events/simple-event.xml";

  private static final String NDC_EVENT_RESOURCE = "/log-events/ndc-event.xml";

  private static final String STACKTRACE_EVENT_RESOURCE = "/log-events/stacktrace-event.xml";

  @Test
  public void testParseSimple() throws Exception {
    final LogEvent expected = new LogEvent(new Date(1471940217427L), Level.INFO, //
        "com.example.MyClass", "main", //
        null, "This is a test message", null);
    assertEquals(expected, PARSER.parse(load(SIMPLE_EVENT_RESOURCE)));
  }

  @Test
  public void testParseNDC() throws Exception {
    final LogEvent expected = new LogEvent(new Date(1471940217427L), Level.INFO, //
        "com.example.MyClass", "main", //
        "my ndc", "This is a test message", null);
    assertEquals(expected, PARSER.parse(load(NDC_EVENT_RESOURCE)));
  }
  
  @Test
  public void testParseStacktrace() throws Exception {
    final LogEvent expected = new LogEvent(new Date(1471940217427L), Level.INFO, //
        "com.example.MyClass", "main", //
        null, "This is a test message", "java.lang.IllegalArgumentException: This a is test");
    assertEquals(expected, PARSER.parse(load(STACKTRACE_EVENT_RESOURCE)));
  }
  
  @Test(expected = IOException.class)
  public void testParseIllegalString() throws Exception {
    PARSER.parse("This is not XML!");
  }

  static String load(String resource) {
    final InputStream is = TestLogEvent.class.getResourceAsStream(resource);
    return new BufferedReader(new InputStreamReader(is))//
        .lines() //
        .collect(Collectors.joining("\n"));
  }
  
  static void assertEquals(LogEvent expected, LogEvent actual) {
    assertNotNull("No log event parsed", actual);
    Assert.assertEquals("logger name", expected.loggerNameProperty().getValue(), actual.loggerNameProperty().getValue());
    Assert.assertEquals("timestamp", expected.timestampProperty().getValue(), actual.timestampProperty().getValue());
    Assert.assertEquals("log level", expected.levelProperty().getValue(), actual.levelProperty().getValue());
    Assert.assertEquals("thread name", expected.threadNameProperty().getValue(), actual.threadNameProperty().getValue());
    Assert.assertEquals("log message", expected.messageProperty().getValue(), actual.messageProperty().getValue());
    Assert.assertEquals("NDC", expected.ndcProperty().getValue(), actual.ndcProperty().getValue());
    Assert.assertEquals("Stack Trace", expected.stacktraceProperty().getValue(), actual.stacktraceProperty().getValue());
  }
}

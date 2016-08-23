package org.levigo.jadice.server.converterclient.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Level;
import org.apache.log4j.xml.XMLLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.levigo.util.base.Strings;

/**
 * Parses an XML snippet serialized with log4j's {@link XMLLayout}.
 */
public class LogEventParser {

  private static LogEventParser INSTANCE;

  private final DocumentBuilder docBuilder;

  private final XPathExpression loggerNameExpr;

  private XPathExpression timestampExpr;

  private XPathExpression levelExpr;

  private XPathExpression threadNameExpr;

  private XPathExpression messageExpr;

  private XPathExpression ndcExpr;

  private XPathExpression throwableExpr;

  private LogEventParser() {
    try {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(false);
      dbf.setNamespaceAware(false);
      docBuilder = dbf.newDocumentBuilder();
      XPath xpath = XPathFactory.newInstance().newXPath();
      loggerNameExpr = xpath.compile("event/@logger");
      timestampExpr = xpath.compile("event/@timestamp");
      levelExpr = xpath.compile("event/@level");
      threadNameExpr = xpath.compile("event/@thread");
      messageExpr = xpath.compile("event/message/text()");
      ndcExpr = xpath.compile("event/NDC/text()");
      throwableExpr = xpath.compile("event/throwable/text()");

    } catch (ParserConfigurationException | XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  public static LogEventParser getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LogEventParser();
    }
    return INSTANCE;
  }

  public LogEvent parse(String xml) throws IOException {
    try {
      if (xml == null || xml.isEmpty()) {
        return null;
      }
      final Document doc = buildDocument(xml);

      final Date timestamp = evalAsDate(timestampExpr, doc, System.currentTimeMillis());
      final String loggerName = eval(loggerNameExpr, doc);
      final String stackTrace = eval(throwableExpr, doc);
      final Level level = Level.toLevel(eval(levelExpr, doc));
      final String threadName = eval(threadNameExpr, doc);
      final String ndc = eval(ndcExpr, doc);
      final String message = eval(messageExpr, doc);

      return new LogEvent(timestamp, level, loggerName, threadName, ndc, message, stackTrace);
    } catch (XPathExpressionException | SAXException e) {
      throw new IOException("Could not unmarshall", e);
    }
  }

  private Document buildDocument(String xml) throws SAXException, IOException {
    return docBuilder.parse(new InputSource(new StringReader(xml)));
  }

  private static String eval(XPathExpression expr, Node node) throws XPathExpressionException {
    final String val = (String) expr.evaluate(node, XPathConstants.STRING);
    return Strings.empty(val) ? null : val;
  }

  private Date evalAsDate(XPathExpression expr, Node node, long fallback) throws XPathExpressionException {
    final String raw = eval(expr, node);
    if (raw == null) {
      return new Date(fallback);
    }
    try {
      return new Date(Long.parseLong(raw));
    } catch (NumberFormatException e) {
      return new Date(fallback);
    }
  }
}

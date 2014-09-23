package org.levigo.jadice.server.converterclient.gui.inspector;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import com.levigo.jadice.server.client.jms.JMSJobFactory;

/**
 * Dummy QueueConnectionFactory that cannot work, but is required to initialize a
 * {@link JMSJobFactory}
 */
public class DummyQueueConnectionFactory implements QueueConnectionFactory {

  private static final String MSG = "This is just a dummy implementation";

  @Override
  public Connection createConnection() throws JMSException {
    throw new JMSException(MSG);
  }

  @Override
  public Connection createConnection(String userName, String password) throws JMSException {
    throw new JMSException(MSG);
  }

  @Override
  public QueueConnection createQueueConnection() throws JMSException {
    throw new JMSException(MSG);
  }

  @Override
  public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
    throw new JMSException(MSG);
  }

}

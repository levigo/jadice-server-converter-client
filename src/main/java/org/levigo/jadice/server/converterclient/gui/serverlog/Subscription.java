package org.levigo.jadice.server.converterclient.gui.serverlog;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class Subscription {

  private final TopicSession session;
  
  private final TopicSubscriber subscriber;
  
  private final TopicConnection connection;

  private final String subscriptionName;
  
  public Subscription(TopicConnection connection, TopicSession session, TopicSubscriber subscriber,
      String subscriptionName) {
    this.session = session;
    this.connection = connection;
    this.subscriber = subscriber;
    this.subscriptionName = subscriptionName;
  }



  public void close() throws JMSException {
      subscriber.close();
      session.unsubscribe(subscriptionName);
      session.close();
      connection.close();
  }

}

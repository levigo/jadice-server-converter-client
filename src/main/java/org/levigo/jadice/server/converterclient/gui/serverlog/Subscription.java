package org.levigo.jadice.server.converterclient.gui.serverlog;

import jakarta.jms.JMSException;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

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

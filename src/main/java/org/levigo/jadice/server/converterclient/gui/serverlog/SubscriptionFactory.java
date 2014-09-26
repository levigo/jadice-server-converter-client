package org.levigo.jadice.server.converterclient.gui.serverlog;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.levigo.jadice.server.converterclient.Preferences;

import com.levigo.jadice.server.util.UUIDGenerator;

public class SubscriptionFactory {
  
  public static final String SUBSCRIBER_NAME = "log_subscribtion";

  private static SubscriptionFactory instance;

  public static SubscriptionFactory getInstance() {
    if (instance == null) {
      instance = new SubscriptionFactory();
    }
    return instance;
  }

  private SubscriptionFactory() {
    // hidden Constr.
  }

  public Subscription createSubscription(String serverLocation, MessageListener listener) throws JMSException {
    if (!Preferences.recentServersProperty().contains(serverLocation)) {
      // Store server URL in history
      Preferences.recentServersProperty().add(serverLocation);
    }
    
    ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(serverLocation);
    connFactory.setUserName(Preferences.jmsUsernameProperty().getValue());
    connFactory.setPassword(Preferences.jmsPasswordProperty().getValue());
    TopicConnection topicConnection = connFactory.createTopicConnection();
    topicConnection.setClientID(UUIDGenerator.getUUID());
    topicConnection.start();
    TopicSession session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    Topic logTopic = session.createTopic(Preferences.jmsLogTopicNameProperty().getValue());
    TopicSubscriber subscriber = session.createDurableSubscriber(logTopic, SUBSCRIBER_NAME);
    subscriber.setMessageListener(listener);

    return new Subscription(topicConnection, session, subscriber, SUBSCRIBER_NAME);

  }

}

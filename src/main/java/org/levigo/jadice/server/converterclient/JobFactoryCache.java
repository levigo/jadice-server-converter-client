package org.levigo.jadice.server.converterclient;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.levigo.jadice.server.ConnectionException;
import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.client.JobFactory;
import com.levigo.jadice.server.client.jms.JMSJobFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.event.CacheEventListenerAdapter;

public class JobFactoryCache {
  
  private static class CacheKey {

    public final String serverLocation;

    public final String jmsUsername;

    public final String jmsPassword;

    public final String jmsRequestQueueName;
    
    public final int jmsJobPriority;


    public CacheKey(String serverLocation, String jmsUsername, String jmsPassword, String jmsRequestQueueName,
        int jmsJobPriority) {
      this.serverLocation = serverLocation;
      this.jmsUsername = jmsUsername;
      this.jmsPassword = jmsPassword;
      this.jmsRequestQueueName = jmsRequestQueueName;
      this.jmsJobPriority = jmsJobPriority;
    }


    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + jmsJobPriority;
      result = prime * result + ((jmsPassword == null) ? 0 : jmsPassword.hashCode());
      result = prime * result + ((jmsRequestQueueName == null) ? 0 : jmsRequestQueueName.hashCode());
      result = prime * result + ((jmsUsername == null) ? 0 : jmsUsername.hashCode());
      result = prime * result + ((serverLocation == null) ? 0 : serverLocation.hashCode());
      return result;
    }


    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheKey other = (CacheKey) obj;
      if (jmsJobPriority != other.jmsJobPriority)
        return false;
      if (jmsPassword == null) {
        if (other.jmsPassword != null)
          return false;
      } else if (!jmsPassword.equals(other.jmsPassword))
        return false;
      if (jmsRequestQueueName == null) {
        if (other.jmsRequestQueueName != null)
          return false;
      } else if (!jmsRequestQueueName.equals(other.jmsRequestQueueName))
        return false;
      if (jmsUsername == null) {
        if (other.jmsUsername != null)
          return false;
      } else if (!jmsUsername.equals(other.jmsUsername))
        return false;
      if (serverLocation == null) {
        if (other.serverLocation != null)
          return false;
      } else if (!serverLocation.equals(other.serverLocation))
        return false;
      return true;
    }
    
    
  }
  private static final JobFactoryCache INSTANCE = new JobFactoryCache();

  private final Ehcache jobFactoryCache;


  private JobFactoryCache() {
    final CacheManager cacheManager = CacheManager.newInstance();
    // FIXME: make TimeToIdle configurable
    Cache tmp = new Cache(JobFactory.class.getSimpleName(), 10, false, false, 0, 60);
    jobFactoryCache = new SelfPopulatingCache(tmp, key -> {
      if (!(key instanceof CacheKey)) {
        throw new IllegalArgumentException("key is not a " + CacheKey.class.getSimpleName());
      }
      CacheKey key2 = (CacheKey) key;

      ActiveMQConnectionFactory amqConnectionFactory = new ActiveMQConnectionFactory(key2.serverLocation);
      amqConnectionFactory.setUserName(key2.jmsUsername);
      amqConnectionFactory.setPassword(key2.jmsPassword);
      final JMSJobFactory result = new JMSJobFactory(amqConnectionFactory, key2.jmsRequestQueueName);
      result.setDefaultPriority(key2.jmsJobPriority);
      return result;
    });
    jobFactoryCache.getCacheEventNotificationService().registerListener(new CacheEventListenerAdapter() {
      @Override
      public void notifyElementEvicted(Ehcache cache, Element element) {
        invalidateElement(element);
      }
      
      @Override
      public void notifyElementExpired(Ehcache cache, Element element) {
        invalidateElement(element);
      }
      
      @Override
      public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        invalidateElement(element);
      }
      
      private void invalidateElement(Element element) {
        if (element.getObjectValue() instanceof JobFactory) {
          JobFactory jf = (JobFactory) element.getObjectValue();
          jf.close();
        }
        
      }
    });
    cacheManager.addCache(jobFactoryCache);
  }

  public static JobFactoryCache getInstance() {
    return INSTANCE;
  }

  private JobFactory getJobFactory(String serverLocation) throws ConnectionException {
    final String jmsUsername = Preferences.jmsUsernameProperty().getValue();
    final String jmsPassword = Preferences.jmsPasswordProperty().getValue();
    final String jmsRequestQueueName = Preferences.jmsRequestQueueNameProperty().getValue();
    final int jmsJobPriority = Preferences.jmsJobPriority().getValue();

    final CacheKey key = new CacheKey(serverLocation, jmsUsername, jmsPassword, jmsRequestQueueName, jmsJobPriority);
    final Element element = jobFactoryCache.get(key);
    if (element == null || !(element.getObjectValue() instanceof JobFactory)) {
      throw new RuntimeException("Could not create a JobFactory. This seems to be a bug");
    }
    final JobFactory jf = (JobFactory) element.getObjectValue();

    if (element.getHitCount() == 0) {
      try {
        jf.connect();
      } catch (ConnectionException e) {
        jobFactoryCache.remove(key);
        throw e;
      }
    }
    return jf;
  }

  public Job createJob(String serverLocation) throws JobException {
    return getJobFactory(serverLocation).createJob();
  }
}

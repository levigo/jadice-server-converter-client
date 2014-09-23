package org.levigo.jadice.server.converterclient.gui.jmx;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.management.AttributeList;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.Query;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

public class JMXHandler implements NotificationListener {
  
  public static enum ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    CONNECTING;
  }

  public interface CallbackHandler {
    
    void connectionEstablished(String serverVersion);
    
    void connectionFailed(Throwable reason);
    
    void connectionClosed();

    void jobStateEventOccured(JobStateEventDTO jobStateEvent);

    void updatePerformanceInfo(PerformanceInfoDTO performanceInfo);
  }
  
  private class PerformanceUpdate implements Runnable {

    private long lastPerformanceUpdate = 0;
    
    private int exceptionsCount = 0;

    @Override
    public void run() {
      if (mbsc == null || status.get() != ConnectionStatus.CONNECTED) {
        return;
      }
      
      boolean lowTraffic = false;
      if (System.currentTimeMillis() - lastJobEvent > LOW_TRAFFIC_PERFORMANCE_UPDATE_INTERVALL) {
        lowTraffic = true;
      }
      
      final long updateIntervall = lowTraffic ? LOW_TRAFFIC_PERFORMANCE_UPDATE_INTERVALL : HIGH_TRAFFIC_PERFOMANCE_UPDATE_INTERVALL;
      
      if (System.currentTimeMillis() - lastPerformanceUpdate > updateIntervall) {
        try {
          final ObjectInstance serverStatisticsBean = getServerStatisticsBean(mbsc);
          final ObjectInstance schedulerBean = getJobSchedulerBean(mbsc);
          PerformanceInfoDTO performanceInfo = getPerformanceInfo(schedulerBean, serverStatisticsBean, mbsc);
          callbackHandler.updatePerformanceInfo(performanceInfo);
          lastPerformanceUpdate = System.currentTimeMillis();
          exceptionsCount = 0;
        } catch (Exception e) {
          exceptionsCount++;
          LOGGER.error("Error on cyclic performance info update", e);
          
          if (exceptionsCount == 5) {
            LOGGER.error("Closing JMX Connection due to too many errors");
            closeConnection();
          }
        }
      }
    }
    
  }

  private static final Logger LOGGER = Logger.getLogger(JMXHandler.class);
  
  private static final long LOW_TRAFFIC_PERFORMANCE_UPDATE_INTERVALL = TimeUnit.SECONDS.toMillis(10);
  
  private static final long HIGH_TRAFFIC_PERFOMANCE_UPDATE_INTERVALL = TimeUnit.SECONDS.toMillis(1);

  private final CallbackHandler callbackHandler;
  
  private final ObjectProperty<ConnectionStatus> status = new SimpleObjectProperty<>(ConnectionStatus.DISCONNECTED);

  private MBeanServerConnection mbsc;
  
  private long lastJobEvent = 0;

  private JMXConnector connector;

  public JMXHandler(CallbackHandler callbackHandler) {
    this.callbackHandler = callbackHandler;
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new PerformanceUpdate(), 0, HIGH_TRAFFIC_PERFOMANCE_UPDATE_INTERVALL, TimeUnit.MILLISECONDS);
  }

  public void openConnection(String url) {
    new Thread(() -> {
    try {
      status.set(ConnectionStatus.CONNECTING);
      JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
      connector = JMXConnectorFactory.connect(jmxUrl, null);
      MBeanServerConnection mbsc = connector.getMBeanServerConnection();
      if (validateConnection(mbsc)) {
        status.set(ConnectionStatus.CONNECTED);
        callbackHandler.connectionEstablished(getServerVersion(getJadiceServerBean(mbsc), mbsc));
        useMBeanServerConnection(mbsc);
      } else {
        status.set(ConnectionStatus.DISCONNECTED);
        callbackHandler.connectionFailed(new IllegalArgumentException("JMX does not connect to an instance of jadice server"));
      }

    } catch (Exception e) {
      LOGGER.error("Could not connect to JMX", e);
      status.set(ConnectionStatus.DISCONNECTED);
      callbackHandler.connectionFailed(e);
    }}).start();
  }

  public void closeConnection() {
    try {
      connector.close();
    } catch (IOException e) {
      LOGGER.error("Error when closing JMX Connection", e);
    } finally {
      connector = null;
      mbsc = null;
    }
    status.set(ConnectionStatus.DISCONNECTED);
  }

  public void clearPerformanceInfo() {
    if (mbsc != null) {
      try {
        clearPerformanceInfo(getServerStatisticsBean(mbsc), mbsc);
      } catch (Exception e) {
        LOGGER.warn("Could not clear performance info", e);
      }
    }
  }
  
  public ReadOnlyObjectProperty<ConnectionStatus> getConnectionStatusProperty() {
    return status;
  }


  private void useMBeanServerConnection(MBeanServerConnection mbsc) {
    try {
      final ObjectInstance statisticsBean = getServerStatisticsBean(mbsc);
      final ObjectInstance schedulerBean = getJobSchedulerBean(mbsc);
      final PerformanceInfoDTO performanceInfo = getPerformanceInfo(schedulerBean, statisticsBean, mbsc);

      callbackHandler.updatePerformanceInfo(performanceInfo);

      mbsc.addNotificationListener(statisticsBean.getObjectName(), this, null, null);
      this.mbsc = mbsc;
    } catch (Exception e) {
      LOGGER.error("Error when establishing MBeanServerConnection", e);
    }
  }

  private boolean validateConnection(MBeanServerConnection mbsc) {
    try {
      return getJadiceServerBean(mbsc) != null;
    } catch (Exception e) {
      LOGGER.error("Error when validating MBeanServerConnection", e);
    }
    return false;
  }
  
  private ObjectInstance getBeanByClassName(String classname, MBeanServerConnection mbsc) throws IOException {
    final Set<ObjectInstance> beans = mbsc.queryMBeans(null,
        Query.isInstanceOf(Query.value(classname)));
    if (beans.size() != 1) {
      return null;
    }
    return beans.iterator().next();
    
  }

  private ObjectInstance getJadiceServerBean(MBeanServerConnection mbsc) throws IOException {
    return getBeanByClassName("com.levigo.jadice.server.core.JadiceServer", mbsc);
  }

  private String getServerVersion(ObjectInstance serverBean, MBeanServerConnection mbsc) throws JMException,
      IOException {
    return mbsc.getAttribute(serverBean.getObjectName(), "Version").toString();
  }

  private ObjectInstance getJobSchedulerBean(MBeanServerConnection mbsc) throws IOException {
    return getBeanByClassName("com.levigo.jadice.server.core.JobScheduler", mbsc);
  }
  
  private ObjectInstance getServerStatisticsBean(MBeanServerConnection mbsc) throws IOException {
    return getBeanByClassName("com.levigo.jadice.server.core.ServerStatistics", mbsc);
  }

  private PerformanceInfoDTO getPerformanceInfo(ObjectInstance schedulerBean, ObjectInstance serverStatisticsBean, MBeanServerConnection mbsc)
      throws JMException, IOException {
    final AttributeList attributes = new AttributeList();
    attributes.addAll(mbsc.getAttributes(schedulerBean.getObjectName(), PerformanceInfoDTO.ATTRIBUTE_NAMES_JOB_SCHEDULER));
    attributes.addAll(mbsc.getAttributes(serverStatisticsBean.getObjectName(), PerformanceInfoDTO.ATTRIBUTE_NAMES_SERVER_STATISTICS));

    return PerformanceInfoDTO.parseAttributsList(attributes);
  }

  private void clearPerformanceInfo(ObjectInstance schedulerBean, MBeanServerConnection mbsc) throws JMException,
      IOException {
    for (String op : new String[]{
        "clearJobRates", "clearExecutionTimes", "clearEfficiency"
    }) {
      mbsc.invoke(schedulerBean.getObjectName(), op, new Object[]{}, new String[]{});
    }
  }

  @Override
  public void handleNotification(Notification notification, Object handback) {
    if (!"com.levigo.jadice.server.core.JobStateEvent".equals(notification.getType()))
      return;

    JobStateEventDTO stateEvent = JobStateEventDTO.parseNotification(notification);
    callbackHandler.jobStateEventOccured(stateEvent);
    lastJobEvent = System.currentTimeMillis();
  }


}

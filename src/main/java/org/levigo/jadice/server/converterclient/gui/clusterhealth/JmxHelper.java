package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.Query;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.levigo.jadice.server.converterclient.Preferences;

public class JmxHelper {

  private static final String SERVER_BEAN_CLASS = "com.levigo.jadice.server.core.JadiceServer";
  
  private static final String STATISTICS_BEAN_CLASS = "com.levigo.jadice.server.core.ServerStatistics";

  private JmxHelper() {
    // hidden constructor
  }

  public static JMXConnector createConnector(String jmxServiceUrl) throws JMException {
    try {
      JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + jmxServiceUrl + "/jmxrmi");
      final Map<String, Object> env = new HashMap<>();
      if (Preferences.jmxUsernameProperty().isNotEmpty().get()) {
        // See https://blogs.oracle.com/lmalventosa/entry/jmx_authentication_authorization
        String[] creds = {
            Preferences.jmxUsernameProperty().get(), Preferences.jmxPasswordProperty().get()
        };
        env.put(JMXConnector.CREDENTIALS, creds);
      }
      return JMXConnectorFactory.connect(jmxUrl, env);
    } catch (IOException | IllegalArgumentException e) {
      throw wrap(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  private static <N extends Number> N retrieveNumericValue(MBeanServerConnection mbsc, String beanClassName, String attributeName,
      N fallbackValue) throws JMException {
    try {
      ObjectInstance bean = getBeanByClassName(beanClassName, mbsc);
      if (bean == null) {
        return fallbackValue;
      }
      final Object attribute = mbsc.getAttribute(bean.getObjectName(), attributeName);
      if (attribute == null) {
        return fallbackValue;
      }
      return (N) attribute;
    } catch (IOException | ClassCastException e) {
      throw wrap(e);
    }
  }

  public static ObjectInstance getJadiceServerBean(MBeanServerConnection mbsc) throws IOException {
    return JmxHelper.getBeanByClassName(SERVER_BEAN_CLASS, mbsc);
  }


  public static ObjectInstance getStatisticsBean(MBeanServerConnection mbsc) throws IOException {
    return getBeanByClassName(STATISTICS_BEAN_CLASS, mbsc);
  }


  public static ObjectInstance getBeanByClassName(String classname, MBeanServerConnection mbsc) throws IOException {
    final Set<ObjectInstance> beans = mbsc.queryMBeans(null, Query.isInstanceOf(Query.value(classname)));
    if (beans.size() != 1) {
      return null;
    }
    return beans.iterator().next();
  }

  public static float getTotalFailureRate(MBeanServerConnection mbsc) throws JMException {
    return retrieveNumericValue(mbsc, STATISTICS_BEAN_CLASS, "TotalFailureRate", Float.NaN);
  }

  public static float getRecentFailureRate(MBeanServerConnection mbsc) throws JMException {
    return retrieveNumericValue(mbsc, STATISTICS_BEAN_CLASS, "RecentFailureRate", Float.NaN);
  }

  public static long getRecentAverageExecutionTime(MBeanServerConnection mbsc) throws JMException {
    return retrieveNumericValue(mbsc, STATISTICS_BEAN_CLASS, "RecentAverageExecutionTime", -1L);
  }

  public static long getAverageExecutionTime(MBeanServerConnection mbsc) throws JMException {
    return retrieveNumericValue(mbsc, STATISTICS_BEAN_CLASS, "AverageExecutionTime", -1L);
  }
  
  public static float getEfficiency10Min(MBeanServerConnection mbsc) throws JMException {
    return retrieveNumericValue(mbsc, STATISTICS_BEAN_CLASS, "Efficiency10Min", Float.NaN);
  }

  public static boolean isRunning(MBeanServerConnection mbsc) throws JMException {
    try {
      final ObjectInstance bean = getJadiceServerBean(mbsc);
      if (bean == null) {
        return false;
      }
      return "true".equalsIgnoreCase(mbsc.getAttribute(bean.getObjectName(), "Running").toString());
    } catch (IOException e) {
      throw wrap(e);
    }
  }

  private static JMException wrap(Exception e) throws JMException {
    if (e instanceof JMException) {
      return (JMException) e;
    }
    final JMException jmException = new JMException(e.getMessage());
    jmException.initCause(e);
    return jmException;
  }
}

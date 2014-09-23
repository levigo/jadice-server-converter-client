package org.levigo.jadice.server.converterclient.gui.jmx;

import javax.management.Notification;

import org.apache.log4j.Logger;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.Job.State;

public class JobStateEventDTO {
  
  private static final Logger LOGGER = Logger.getLogger(JobStateEventDTO.class);
  
  public String id;
  
  public State state;
  
  public long age;
  
  public String type;
  
  public static JobStateEventDTO parseNotification(Notification notification) {
    final JobStateEventDTO result = new JobStateEventDTO();
    final String userData = notification.getUserData().toString();
    
    LOGGER.debug("Parsing userData: " + userData);
    for (String s : userData.split(";")) {
      final String key = s.split("=", 2)[0];
      final String value = s.split("=", 2)[1];
      
      switch (key) {
        case "id": 
          result.id = value;
          break;
        case "state":
          result.state = Job.State.valueOf(value);
          break;
        case "age": 
          result.age = Long.parseLong(value);
          break;
        case "type": 
          result.type = value;
          break;
        default:
          LOGGER.warn("Unsupported JMX notification userData key " + key);
          break;
      }
    }
    
    return result;
  }
}

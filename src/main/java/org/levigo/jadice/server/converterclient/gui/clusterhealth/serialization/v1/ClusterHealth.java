package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1;

import java.util.ArrayList;
import java.util.List;

public class ClusterHealth {
  
  public static final String CURRENT_VERSION = "1.0";
  
  public String version = CURRENT_VERSION;
  
  public List<Rule<?>> rules = new ArrayList<>();
  
  public List<String> instances = new ArrayList<>();
  
  public boolean autoUpdateEnabled;
  
  public int autoUpdateInterval;

}

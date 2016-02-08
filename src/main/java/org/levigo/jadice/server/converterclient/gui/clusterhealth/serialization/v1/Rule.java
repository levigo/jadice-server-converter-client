package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Rule<N> {
  
  public String implementation;

  public N limit;
  
  public boolean enabled;
}

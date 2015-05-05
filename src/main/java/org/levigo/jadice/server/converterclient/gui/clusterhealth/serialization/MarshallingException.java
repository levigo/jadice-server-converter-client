package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization;

public class MarshallingException extends Exception {

  private static final long serialVersionUID = 1L;
  
  public MarshallingException(String message, Exception cause) {
    super(message, cause);
  }

  public MarshallingException(String message) {
    super(message);
  }
  
}

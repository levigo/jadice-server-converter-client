package org.levigo.jadice.server.converterclient.updatecheck;

public class UpdateCheckException extends Exception {
  
  private static final long serialVersionUID = 1L;

  public UpdateCheckException(Exception reason) {
    super("Could not perform update check", reason);
  }

}

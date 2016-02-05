package org.levigo.jadice.server.converterclient.util.validation;


public class LongValidator extends NumberValidator {
  
  public boolean isNumber(String value) {
    try {
      Long.parseLong(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  public boolean isPositive(String value) {
    return Long.parseLong(value) > 0;
  }
}

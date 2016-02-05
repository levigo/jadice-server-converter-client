package org.levigo.jadice.server.converterclient.util.validation;


public class IntegerValidator extends NumberValidator {

  public boolean isNumber(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  boolean isPositive(String value) {
    return Integer.parseInt(value) > 0;
  }
}

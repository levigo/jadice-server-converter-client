package org.levigo.jadice.server.converterclient.gui.conversion;


public class IntegerValidator extends DisableableNumberValidator<String> {

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

package org.levigo.jadice.server.converterclient.util.validation;


public class FloatValidator extends NumberValidator<String> {

  public boolean isNumber(String value) {
    try {
      Float.parseFloat(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  boolean isPositive(String value) {
    return Float.parseFloat(value) > 0.0f;
  }
}

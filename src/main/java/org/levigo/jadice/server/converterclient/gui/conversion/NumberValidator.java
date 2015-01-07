package org.levigo.jadice.server.converterclient.gui.conversion;

import javafx.scene.control.Control;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

public abstract class NumberValidator<T> implements Validator<T> {
  
  private static final ValidationResult OK = new ValidationResult();
  
  private static final String NULL_VALUE_MESSAGE = "value must not be empty";
  
  private static final String NOT_NUMERIC_MESSAGE = "value must be numeric";
  
  private static final String NOT_POSITIVE_MESSAGE = "value must be positive";

  @Override
  public ValidationResult apply(Control control, T value) {
    if (value == null || value.toString().isEmpty()) {
      return ValidationResult.fromWarning(control, NULL_VALUE_MESSAGE);
    }
    if (!isNumber(value)) {
      return ValidationResult.fromError(control, NOT_NUMERIC_MESSAGE);
    }
    if (!isPositive(value)) {
      return ValidationResult.fromWarning(control, NOT_POSITIVE_MESSAGE);
    }
    
    return OK;
  }
  
  abstract boolean isNumber(T value);
  
  abstract boolean isPositive(T value);

}

package org.levigo.jadice.server.converterclient.gui.conversion;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;
import javafx.scene.control.Control;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

public abstract class NumberValidator<T> implements Validator<T> {
  
  private static final ValidationResult OK = new ValidationResult();
  
  @Override
  public ValidationResult apply(Control control, T value) {
    if (value == null || value.toString().isEmpty()) {
      return ValidationResult.fromWarning(control, getUiResources().getString("validator.null-value.message"));
    }
    if (!isNumber(value)) {
      return ValidationResult.fromError(control, getUiResources().getString("validator.not-numeric.message"));
    }
    if (!isPositive(value)) {
      return ValidationResult.fromWarning(control, getUiResources().getString("validator.not-positive.message"));
    }
    
    return OK;
  }
  
  abstract boolean isNumber(T value);
  
  abstract boolean isPositive(T value);

}

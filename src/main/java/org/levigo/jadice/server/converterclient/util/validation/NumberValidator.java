package org.levigo.jadice.server.converterclient.util.validation;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;
import javafx.scene.control.Control;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import com.levigo.util.base.Strings;

public abstract class NumberValidator implements Validator<String> {
  
  private static final ValidationResult OK = new ValidationResult();
  
  @Override
  public ValidationResult apply(Control control, String value) {
    if (Strings.emptyTrim(value)) {
      return ValidationResult.fromError(control, getUiResources().getString("validator.null-value.message"));
    }
    if (!isNumber(value)) {
      return ValidationResult.fromError(control, getUiResources().getString("validator.not-numeric.message"));
    }
    if (!isPositive(value)) {
      return ValidationResult.fromWarning(control, getUiResources().getString("validator.not-positive.message"));
    }
    
    return OK;
  }
  
  abstract boolean isNumber(String value);
  
  abstract boolean isPositive(String value);

}

package org.levigo.jadice.server.converterclient.util.validation;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import com.levigo.util.base.Strings;

import javafx.scene.control.Control;

public class EmptyStringValidator implements Validator<String> {
  
  private static final String MSG = getUiResources().getString("validator.null-value.message");

  @Override
  public ValidationResult apply(Control t, String u) {
    return ValidationResult.fromErrorIf(t, MSG, Strings.emptyTrim(u));
  }

}

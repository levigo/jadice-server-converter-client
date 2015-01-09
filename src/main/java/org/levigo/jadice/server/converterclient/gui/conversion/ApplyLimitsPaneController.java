package org.levigo.jadice.server.converterclient.gui.conversion;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.apache.log4j.Logger;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;

import com.levigo.jadice.server.Limit;
import com.levigo.jadice.server.NodeCountLimit;
import com.levigo.jadice.server.PageCountLimit;
import com.levigo.jadice.server.StreamCountLimit;
import com.levigo.jadice.server.StreamSizeLimit;
import com.levigo.jadice.server.TimeLimit;

public class ApplyLimitsPaneController {
  
  private static final Logger LOGGER = Logger.getLogger(ApplyLimitsPaneController.class);
  
  @FXML
  private CheckBox timeLimitCB;
  
  @FXML
  private TextField timeLimitValue;
  
  @FXML
  private ChoiceBox<TimeUnit> timeLimitUnit;
  
  @FXML
  private CheckBox streamSizeLimitCB;
  
  @FXML
  private TextField streamSizeLimitValue;
  
  @FXML
  private CheckBox streamCountLimitCB;
  
  @FXML
  private TextField streamCountLimitValue;
  
  @FXML
  private CheckBox nodeCountLimitCB;
  
  @FXML
  private TextField nodeCountLimitValue;
  
  @FXML
  private CheckBox pageCountLimitCB;

  @FXML
  private TextField pageCountLimitValue;
  
  private final ObservableList<Limit> effectiveLimits = FXCollections.observableArrayList();
  
  @FXML
  protected void initialize() {
    timeLimitUnit.itemsProperty().getValue().addAll(EnumSet.of(MILLISECONDS, SECONDS, MINUTES));
    timeLimitUnit.setValue(SECONDS);
    timeLimitUnit.setConverter(new StringConverter<TimeUnit>() {
      @Override
      public String toString(TimeUnit object) {
        return object.toString().toLowerCase();
      }
      
      @Override
      public TimeUnit fromString(String string) {
        return TimeUnit.valueOf(string.toUpperCase());
      }
    });
    
    final ChangeListener<Limit> limitChangeHandler = (observable, oldValue, newValue) -> {
      if (oldValue != null) {
        effectiveLimits.remove(oldValue);
      }
      if (newValue != null) {
        effectiveLimits.add(newValue);
      }
    };
    
    final TimeLimitHandler timeLimitHandler = new TimeLimitHandler();
    timeLimitHandler.limitProperty().addListener(limitChangeHandler);
    
    final SimpleLimitHandler<Long, StreamSizeLimit> streamSizeLimitHandler = new SimpleLimitHandler<>(streamSizeLimitCB, streamSizeLimitValue, Long::parseLong, StreamSizeLimit::new, new LongValidator());
    streamSizeLimitHandler.limitProperty().addListener(limitChangeHandler);

    final SimpleLimitHandler<Integer, StreamCountLimit> streamCountLimitHandler = new SimpleLimitHandler<>(streamCountLimitCB, streamCountLimitValue, Integer::parseInt, StreamCountLimit::new, new IntegerValidator());
    streamCountLimitHandler.limitProperty().addListener(limitChangeHandler);
    
    final SimpleLimitHandler<Integer, NodeCountLimit> nodeCountLimitHandler = new SimpleLimitHandler<>(nodeCountLimitCB, nodeCountLimitValue, Integer::parseInt, NodeCountLimit::new, new IntegerValidator());
    nodeCountLimitHandler.limitProperty().addListener(limitChangeHandler);
    
    final SimpleLimitHandler<Integer, PageCountLimit> pageCountLimitHandler = new SimpleLimitHandler<>(pageCountLimitCB, pageCountLimitValue, Integer::parseInt, PageCountLimit::new, new IntegerValidator());
    pageCountLimitHandler.limitProperty().addListener(limitChangeHandler);
  }

  public ObservableList<Limit> getLimits() {
    return effectiveLimits;
  }
  
  private class TimeLimitHandler {
    private final ValidationSupport validationSupport = new ValidationSupport();
    private final ObjectProperty<TimeLimit> limitProperty = new SimpleObjectProperty<>(null);
    
    public TimeLimitHandler() {
      registerValidator(timeLimitValue, timeLimitCB, validationSupport, new LongValidator());
      
      timeLimitCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
        buildLimit();
      });
      timeLimitValue.textProperty().addListener((observable, oldValue, newValue) -> {
        Platform.runLater(() -> {
          // Run Later because validation support also runs later :-/
          buildLimit();
        });
      });
      timeLimitUnit.valueProperty().addListener((observable, oldValue, newValue) -> {
        buildLimit();
      });
    }

    private void buildLimit() {
      if (!timeLimitCB.isSelected() || !isInputValid(validationSupport)) {
        limitProperty.set(null);
        return;
      }

      final TimeLimit limit = new TimeLimit(Long.parseLong(timeLimitValue.getText()), timeLimitUnit.getValue());
      LOGGER.debug("Created new limit: " + limit);
      limitProperty.set(limit);
    }
    
    public ObjectProperty<TimeLimit> limitProperty() {
      return limitProperty;
    }
  }
  
  private static class SimpleLimitHandler<N extends Number, L extends Limit> {
    private final ValidationSupport validationSupport = new ValidationSupport();

    private final CheckBox checkbox;
    private final TextField valueField;
    private final Parser<N> parser;
    private final Constructor<N, L> constr;

    private final ObjectProperty<L> limitProperty = new SimpleObjectProperty<>(null);
    
    public SimpleLimitHandler(CheckBox checkbox, TextField valueField, Parser<N> parser, Constructor<N, L> constr, NumberValidator<String> validator) {
      this.checkbox = checkbox;
      this.valueField = valueField;
      this.parser = parser;
      this.constr = constr;
      
      registerValidator(valueField, checkbox, validationSupport, validator);
      
      checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
        buildLimit();
      });
      valueField.textProperty().addListener((observable, oldValue, newValue) -> {
        Platform.runLater(() -> {
          // Run Later because validation support also runs later :-/
          buildLimit();
        });
      });
    }
    
    private void buildLimit() {
      if (!checkbox.isSelected() || !isInputValid(validationSupport)) {
        limitProperty.set(null);
        return;
      }

      final L limit = constr.create(parser.parse(valueField.getText()));
      LOGGER.debug("Created new limit: " + limit);
      limitProperty.set(limit);
    }
    
    public ObjectProperty<L> limitProperty() {
      return limitProperty;
    }
  }
  
  private static boolean isInputValid(ValidationSupport support) {
    final ValidationResult validationRes = support.getValidationResult();
    return !support.isInvalid() && 
        (validationRes != null && validationRes.getWarnings().isEmpty() && validationRes.getErrors().isEmpty());
  }
  
  private static void registerValidator(TextField field, CheckBox cb, ValidationSupport support, NumberValidator<String> validator) {
    cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
      ValidationSupport.setRequired(field, newValue);
    });
    support.errorDecorationEnabledProperty().bind(cb.selectedProperty());
    support.registerValidator(field, cb.isSelected(), validator);
  }
  
  @FunctionalInterface
  private static interface Constructor<N extends Number, L extends Limit> {
    L create(N v);
  }
  
  @FunctionalInterface
  private static interface Parser<N extends Number> {
    N parse(String s);
  }
}

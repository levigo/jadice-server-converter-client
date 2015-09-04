package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import org.apache.log4j.Logger;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.NumericRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentAverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentEfficiencyRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ServerRunningRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule;
import org.levigo.jadice.server.converterclient.util.validation.FloatValidator;
import org.levigo.jadice.server.converterclient.util.validation.LongValidator;
import org.levigo.jadice.server.converterclient.util.validation.NumberValidator;

import com.sun.javafx.binding.ObjectConstant;

import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ConfigureClusterHealthWarningsPaneController {
  
  private static final Logger LOGGER = Logger.getLogger(ConfigureClusterHealthWarningsPaneController.class);
  
  @FXML
  CheckBox serverRunningCB;
  
  @FXML
  CheckBox totalFailureRateCB;
  
  @FXML
  TextField totalFailureRateValue;
  
  @FXML
  CheckBox recentFailureRateCB;
  
  @FXML
  TextField recentFailureRateValue;
  
  @FXML
  CheckBox avgExecTimeCB;
  
  @FXML
  TextField avgExecTimeValue;
  
  @FXML
  CheckBox recentExecTimeCB;
  
  @FXML
  TextField recentExecTimeValue;
  
  @FXML
  CheckBox recentEfficiencyCB;
  
  @FXML
  TextField recentEfficiencyValue;
  
  @FXML
  CheckBox autoUpdateCB;
  
  @FXML
  TextField autoUpdateValue;
  
  @FXML
  protected void initialize() {
    final ChangeListener<Rule<?>> limitChangeHandler = (observable, oldValue, newValue) -> {
      if (oldValue != null) {
        LOGGER.debug(String.format("Remove rule '%s'", oldValue));
        Preferences.clusterHealthProperty().getValue().rules.remove(oldValue);
      }
      if (newValue != null) {
        LOGGER.debug(String.format("Add rule '%s'", newValue.toString()));
        Preferences.clusterHealthProperty().getValue().rules.add(newValue);
      }
    };

    final ServerRunningRuleHandler serverRunningHandler = new ServerRunningRuleHandler();
    serverRunningHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Float, TotalFailureRateRule> totalFalureRateHandler = new FloatRuleHandler<>(totalFailureRateCB, totalFailureRateValue, TotalFailureRateRule::new);
    totalFalureRateHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Float, RecentFailureRateRule> recentFailureRateHandler = new FloatRuleHandler<>(recentFailureRateCB, recentFailureRateValue, RecentFailureRateRule::new);
    recentFailureRateHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Long, AverageExecutionTimeRule> avgExecTimeHandler = new LongRuleHandler<>(avgExecTimeCB, avgExecTimeValue, AverageExecutionTimeRule::new);
    avgExecTimeHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Long, RecentAverageExecutionTimeRule> recentExecTimeHandler = new LongRuleHandler<>(recentExecTimeCB, recentExecTimeValue, RecentAverageExecutionTimeRule::new);
    recentExecTimeHandler.ruleProperty().addListener(limitChangeHandler);
    
    final SimpleRuleHandler<Float, RecentEfficiencyRule> recentEfficiencyHandler = new FloatRuleHandler<>(recentEfficiencyCB, recentEfficiencyValue, RecentEfficiencyRule::new);
    recentEfficiencyHandler.ruleProperty().addListener(limitChangeHandler);
  }

  private class ServerRunningRuleHandler {
    
    private final ServerRunningRule theRule = new ServerRunningRule();

    private final ObjectProperty<ServerRunningRule> ruleProperty = new SimpleObjectProperty<>(null);

    public ServerRunningRuleHandler() {
      ruleProperty.bind(new When(serverRunningCB.selectedProperty()).then(theRule).otherwise(ObjectConstant.valueOf(null)));
    }

    public ObjectProperty<ServerRunningRule> ruleProperty() {
      return ruleProperty;
    }
  }
  
  private static class FloatRuleHandler<R extends NumericRule<Float>> extends SimpleRuleHandler<Float, R> {
    public FloatRuleHandler(CheckBox checkbox, TextField valueField, Constructor<Float, R> constr) {
      super(checkbox, valueField, Float::parseFloat, constr, new FloatValidator());
    }
  }
  
  private static class LongRuleHandler<R extends NumericRule<Long>> extends SimpleRuleHandler<Long, R> {
    public LongRuleHandler(CheckBox checkbox, TextField valueField, Constructor<Long, R> constr) {
      super(checkbox, valueField, Long::parseLong, constr, new LongValidator());
    }
  }
  
  private static class SimpleRuleHandler<N extends Number, R extends NumericRule<N>> {
    private final ValidationSupport validationSupport = new ValidationSupport();

    private final CheckBox checkbox;
    private final TextField valueField;
    private final Parser<N> parser;
    private final Constructor<N, R> constr;

    private final ObjectProperty<R> limitProperty = new SimpleObjectProperty<>(null);
    
    public SimpleRuleHandler(CheckBox checkbox, TextField valueField, Parser<N> parser, Constructor<N, R> constr, NumberValidator<String> validator) {
      this.checkbox = checkbox;
      this.valueField = valueField;
      this.parser = parser;
      this.constr = constr;
      
      registerValidator(valueField, checkbox, validationSupport, validator);
      
      checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
        buildRule();
      });
      valueField.textProperty().addListener((observable, oldValue, newValue) -> {
        Platform.runLater(() -> {
          // Run Later because validation support also runs later :-/
          buildRule();
        });
      });
    }
    
    private void buildRule() {
      if (!checkbox.isSelected() || !isInputValid(validationSupport)) {
        limitProperty.set(null);
        return;
      }

      final R limit = constr.create(parser.parse(valueField.getText()));
      LOGGER.debug("Created new limit: " + limit);
      limitProperty.set(limit);
    }
    
    public ObjectProperty<R> ruleProperty() {
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
  private static interface Constructor<N extends Number, L extends NumericRule<N>> {
    L create(N v);
  }
  
  @FunctionalInterface
  private static interface Parser<N extends Number> {
    N parse(String s);
  }
}

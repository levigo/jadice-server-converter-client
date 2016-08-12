package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AbstractNumericRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentAverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentEfficiencyRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ServerRunningRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.ClusterHealthDTO;
import org.levigo.jadice.server.converterclient.util.validation.FloatValidator;
import org.levigo.jadice.server.converterclient.util.validation.IntegerValidator;
import org.levigo.jadice.server.converterclient.util.validation.LongValidator;
import org.levigo.jadice.server.converterclient.util.validation.NumberValidator;

import com.sun.javafx.binding.ObjectConstant;

import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
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
  
  // Just instantiate; it will perform it's purpose without any further interaction
  @SuppressWarnings("unused")
  private AutoUpdateChangeHandler autoUpdateChangeHandler;
  
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

    final ServerRunningRuleHandler serverRunningHandler = new ServerRunningRuleHandler(existingRuleOf(ServerRunningRule.class));
    serverRunningHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Float, TotalFailureRateRule> totalFalureRateHandler = new FloatRuleHandler<>(existingRuleOf(TotalFailureRateRule.class), totalFailureRateCB, totalFailureRateValue, TotalFailureRateRule::new);
    totalFalureRateHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Float, RecentFailureRateRule> recentFailureRateHandler = new FloatRuleHandler<>(existingRuleOf(RecentFailureRateRule.class), recentFailureRateCB, recentFailureRateValue, RecentFailureRateRule::new);
    recentFailureRateHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Long, AverageExecutionTimeRule> avgExecTimeHandler = new LongRuleHandler<>(existingRuleOf(AverageExecutionTimeRule.class), avgExecTimeCB, avgExecTimeValue, AverageExecutionTimeRule::new);
    avgExecTimeHandler.ruleProperty().addListener(limitChangeHandler);

    final SimpleRuleHandler<Long, RecentAverageExecutionTimeRule> recentExecTimeHandler = new LongRuleHandler<>(existingRuleOf(RecentAverageExecutionTimeRule.class), recentExecTimeCB, recentExecTimeValue, RecentAverageExecutionTimeRule::new);
    recentExecTimeHandler.ruleProperty().addListener(limitChangeHandler);
    
    final SimpleRuleHandler<Float, RecentEfficiencyRule> recentEfficiencyHandler = new FloatRuleHandler<>(existingRuleOf(RecentEfficiencyRule.class), recentEfficiencyCB, recentEfficiencyValue, RecentEfficiencyRule::new);
    recentEfficiencyHandler.ruleProperty().addListener(limitChangeHandler);
    
    autoUpdateChangeHandler = new AutoUpdateChangeHandler(autoUpdateCB, autoUpdateValue);
  }
  
  
  @SuppressWarnings("unchecked")
  public static <T extends Rule<?>> Optional<T> existingRuleOf(Class<T> clazz) {
    final FilteredList<Rule<?>> filtered = Preferences.clusterHealthProperty().getValue().rules.filtered(r -> Objects.equals(clazz, r.getClass()));
    switch (filtered.size()){
      case 0 :
        return Optional.empty();
        
      case 1 :
        return Optional.of((T) filtered.get(0));

      default :
        T rule = (T) filtered.get(0);
        LOGGER.warn(String.format("There are %d rules of type %s defined! Using only '%s'.", filtered.size(), clazz.getSimpleName(), rule));
        return Optional.of(rule);
    }
  }
  
  private class AutoUpdateChangeHandler {
    
    private final ValidationSupport validationSupport = new ValidationSupport();
    
    public AutoUpdateChangeHandler(CheckBox cb, TextField valueField) {
      final ClusterHealthDTO dto = Preferences.clusterHealthProperty().getValue();
      cb.setSelected(dto.autoUpdateEnabled.get());
      valueField.setText(dto.autoUpdateInterval.getValue().toString());

      dto.autoUpdateEnabled.bindBidirectional(cb.selectedProperty());
      registerValidator(valueField, cb, new IntegerValidator());
      
      valueField.textProperty().addListener((observable, oldValue, newValue) -> {
        Platform.runLater(() -> {
          // Run Later because validation support also runs later :-/
          if (!isInputValid()) {
            return;
          }
          int interval = (Integer.parseInt(valueField.getText()));
          LOGGER.debug("Change update interval: " + interval);
          dto.autoUpdateInterval.set(interval);
        });
      });
    }
    
    private boolean isInputValid() {
      final ValidationResult validationRes = validationSupport.getValidationResult();
      return !validationSupport.isInvalid() && 
          (validationRes != null && validationRes.getWarnings().isEmpty() && validationRes.getErrors().isEmpty());
    }
      
      
    
    private void registerValidator(TextField field, CheckBox cb, NumberValidator validator) {
      cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
        ValidationSupport.setRequired(field, newValue);
      });
      validationSupport.errorDecorationEnabledProperty().bind(cb.selectedProperty());
      validationSupport.registerValidator(field, cb.isSelected(), validator);
    }
  }

  private class ServerRunningRuleHandler {
    
    private final ObjectProperty<ServerRunningRule> ruleProperty = new SimpleObjectProperty<>(null);
    
    public ServerRunningRuleHandler(Optional<ServerRunningRule> rule) {
      if (rule.isPresent()) {
        serverRunningCB.setSelected(true);
        ruleProperty.set(rule.get());
      }

      ruleProperty.bind(new When(serverRunningCB.selectedProperty()).then(ServerRunningRule.INSTANCE).otherwise(ObjectConstant.valueOf(null)));
    }

    public ObjectProperty<ServerRunningRule> ruleProperty() {
      return ruleProperty;
    }
  }
  
  private static class FloatRuleHandler<R extends AbstractNumericRule<Float>> extends SimpleRuleHandler<Float, R> {
    public FloatRuleHandler(Optional<R> existingRule, CheckBox checkbox, TextField valueField, Constructor<Float, R> constr) {
      super(existingRule, checkbox, valueField, Float::parseFloat, constr, new FloatValidator());
    }
  }
  
  private static class LongRuleHandler<R extends AbstractNumericRule<Long>> extends SimpleRuleHandler<Long, R> {
    public LongRuleHandler(Optional<R> existingRule, CheckBox checkbox, TextField valueField, Constructor<Long, R> constr) {
      super(existingRule, checkbox, valueField, Long::parseLong, constr, new LongValidator());
    }
  }
  
  private static class SimpleRuleHandler<N extends Number & Comparable<N>, R extends AbstractNumericRule<N>> {
    private final ValidationSupport validationSupport = new ValidationSupport();

    private final CheckBox checkbox;
    private final TextField valueField;
    private final Parser<N> parser;
    private final Constructor<N, R> constr;

    private final ObjectProperty<R> limitProperty = new SimpleObjectProperty<>(null);
    
    public SimpleRuleHandler(Optional<R> existingRule, CheckBox checkbox, TextField valueField, Parser<N> parser, Constructor<N, R> constr, NumberValidator validator) {
      this.checkbox = checkbox;
      this.valueField = valueField;
      this.parser = parser;
      this.constr = constr;
      
      if (existingRule.isPresent()) {
        checkbox.setSelected(existingRule.get().isEnabled());
        valueField.setText(existingRule.get().getLimit().toString());
        limitProperty.set(existingRule.get());
      }
      
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
      if (!checkbox.isSelected() && valueField.getText().isEmpty()) {
        // Clear rule
        limitProperty.set(null);
        return;
      }
      
      if (!isInputValid(validationSupport)) {
        // Don't change the current limit rule
        return;
      }

      final R limit = constr.create(parser.parse(valueField.getText()), checkbox.isSelected());
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
  
  private static void registerValidator(TextField field, CheckBox cb, ValidationSupport support, NumberValidator validator) {
    cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
      ValidationSupport.setRequired(field, newValue);
    });
    support.errorDecorationEnabledProperty().bind(cb.selectedProperty());
    support.registerValidator(field, cb.isSelected(), validator);
  }
  
  @FunctionalInterface
  private static interface Constructor<N extends Number & Comparable<N>, L extends AbstractNumericRule<N>> {
    L create(N v, boolean isEnabled);
  }
  
  @FunctionalInterface
  private static interface Parser<N extends Number> {
    N parse(String s);
  }
}

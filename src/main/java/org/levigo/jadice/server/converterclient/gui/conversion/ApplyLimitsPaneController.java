package org.levigo.jadice.server.converterclient.gui.conversion;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import org.apache.log4j.Logger;
import org.controlsfx.validation.ValidationSupport;

import com.levigo.jadice.server.Limit;
import com.levigo.jadice.server.NodeCountLimit;
import com.levigo.jadice.server.PageCountLimit;
import com.levigo.jadice.server.StreamCountLimit;
import com.levigo.jadice.server.StreamSizeLimit;
import com.levigo.jadice.server.TimeLimit;

public class ApplyLimitsPaneController implements Initializable {
  
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
  
  private final ValidationSupport validationSupport = new ValidationSupport();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    timeLimitUnit.itemsProperty().getValue().addAll(TimeUnit.values());
    timeLimitUnit.setValue(TimeUnit.SECONDS);
    
    validationSupport.setErrorDecorationEnabled(true);
    registerValidator(timeLimitValue, timeLimitCB, new LongValidator());
    registerValidator(streamSizeLimitValue, streamSizeLimitCB, new LongValidator());
    registerValidator(streamCountLimitValue, streamCountLimitCB, new IntegerValidator());
    registerValidator(nodeCountLimitValue, nodeCountLimitCB, new IntegerValidator());
    registerValidator(pageCountLimitValue, pageCountLimitCB, new IntegerValidator());
  }
  
  private void registerValidator(TextField field, CheckBox cb, DisableableNumberValidator<String> validator) {
    cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
      ValidationSupport.setRequired(field, newValue);
      
      // "Change" the text in order to force a revalidation
      final String tmp = field.getText();
      field.setText(null);
      field.setText(tmp);
    });
    validator.enabledProperty().bind(cb.selectedProperty());
    validationSupport.registerValidator(field, cb.isSelected(), validator);
  }
  
  public Collection<Limit> buildLimits() {
    if (validationSupport.isInvalid()) {
      LOGGER.warn("User Input is not valid. Do not apply limits at all");
      return Collections.emptySet();
    }
    
    List<Limit> result = new ArrayList<>();
    if (timeLimitCB.isSelected()) {
      LOGGER.debug("Apply Time Limit: " + timeLimitValue.getText() + " " + timeLimitUnit.getValue());
      result.add(new TimeLimit(parseLong(timeLimitValue.getText()), timeLimitUnit.getValue()));
    }
    if (streamSizeLimitCB.isSelected()) {
      LOGGER.debug("Apply Stream Size Limit: " + streamSizeLimitValue.getText());
      result.add(new StreamSizeLimit(parseLong(streamSizeLimitValue.getText())));
    }
    if (streamCountLimitCB.isSelected()) {
      LOGGER.debug("Apply Stream Count Limit: " + streamCountLimitValue.getText());
      result.add(new StreamCountLimit(parseInt(streamCountLimitValue.getText())));
    }
    if (nodeCountLimitCB.isSelected()) {
      LOGGER.debug("Apply Node Count Limit: " + nodeCountLimitValue.getText());
      result.add(new NodeCountLimit(parseInt(nodeCountLimitValue.getText())));
    }
    if (pageCountLimitCB.isSelected()) {
      LOGGER.debug("Apply Page Count Limit: " + pageCountLimitValue.getText());
      result.add(new PageCountLimit(parseInt(pageCountLimitValue.getText())));
    }
    return result;
  }
}

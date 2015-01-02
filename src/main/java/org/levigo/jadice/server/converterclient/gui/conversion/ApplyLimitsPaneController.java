package org.levigo.jadice.server.converterclient.gui.conversion;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.net.URL;
import java.util.ArrayList;
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
  CheckBox timeLimitCB;
  
  @FXML
  TextField timeLimitValue;
  
  @FXML
  ChoiceBox<TimeUnit> timeLimitUnit;
  
  @FXML
  CheckBox streamSizeLimitCB;
  
  @FXML
  TextField streamSizeLimitValue;
  
  @FXML
  CheckBox streamCountLimitCB;
  
  @FXML
  TextField streamCountLimitValue;
  
  @FXML
  CheckBox nodeCountLimitCB;
  
  @FXML
  TextField nodeCountLimitValue;
  
  @FXML
  CheckBox pageCountLimitCB;

  @FXML
  TextField pageCountLimitValue;
  
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
  
  public List<Limit> buildLimits() {
    List<Limit> result = new ArrayList<>();
    if (timeLimitCB.isSelected()) {
      LOGGER.info("Time Limit" + timeLimitValue.getText() + " " + timeLimitUnit.getValue());
      result.add(new TimeLimit(parseLong(timeLimitValue.getText()), timeLimitUnit.getValue()));
    }
    if (streamSizeLimitCB.isSelected()) {
      LOGGER.info("Stream Size Limit " + streamSizeLimitValue.getText());
      result.add(new StreamSizeLimit(parseLong(timeLimitValue.getText())));
    }
    if (streamCountLimitCB.isSelected()) {
      LOGGER.info("Stream Count Limit" + streamCountLimitValue.getText());
      result.add(new StreamCountLimit(parseInt(streamCountLimitValue.getText())));
    }
    if (nodeCountLimitCB.isSelected()) {
      LOGGER.info("Node Count Limit " + nodeCountLimitValue.getText());
      result.add(new NodeCountLimit(parseInt(nodeCountLimitValue.getText())));
    }
    if (pageCountLimitCB.isSelected()) {
      LOGGER.info("Page Count Limit " + pageCountLimitValue.getText());
      result.add(new PageCountLimit(parseInt(pageCountLimitValue.getText())));
    }
    return result;
  }
}

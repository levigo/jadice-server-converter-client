package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import org.apache.log4j.Logger;
import org.controlsfx.validation.ValidationSupport;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.util.validation.EmptyStringValidator;
import org.levigo.jadice.server.converterclient.util.validation.IntegerValidator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddInstanceController {
  
  private static final Logger LOGGER = Logger.getLogger(AddInstanceController.class);
  
  private final ValidationSupport validation = new ValidationSupport();
  
  @FXML
  private TextField hostname;
  
  @FXML
  private TextField port;
  
  @FXML
  private Button addInstance;
  
  @FXML
  protected void initialize() {
    ValidationSupport.setRequired(hostname, true);
    ValidationSupport.setRequired(port, true);
    validation.registerValidator(hostname, new EmptyStringValidator());
    validation.registerValidator(port, new IntegerValidator());
    
    validation.invalidProperty().addListener((target, oldValue, newValue) -> {
      LOGGER.debug("Invalid? " + oldValue + " -> " + newValue);
      addInstance.setDisable(newValue);
    });
    validation.initInitialDecoration();
    validation.redecorate();
  }
  
  @FXML
  private void onAddInstance() {
    final String jmxName = hostname.getText() + ":" + port.getText();
    LOGGER.info(String.format("Adding %s to monitor cluster health", jmxName));
    Preferences.clusterHealthProperty().getValue().instances.add(jmxName);
    reset();
  }
  
  private void reset() {
    hostname.clear();
    port.clear();
    hostname.requestFocus();
  }

}

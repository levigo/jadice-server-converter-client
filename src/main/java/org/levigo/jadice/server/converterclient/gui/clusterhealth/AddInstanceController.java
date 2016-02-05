package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import org.apache.log4j.Logger;
import org.levigo.jadice.server.converterclient.Preferences;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddInstanceController {
  
  private static final Logger LOGGER = Logger.getLogger(AddInstanceController.class);
  
  @FXML
  private TextField hostname;
  
  @FXML
  private TextField port;
  
  @FXML
  private void onAddInstance() {
    final String jmxName = hostname.getText() + ":" + port.getText();
    LOGGER.info(String.format("Adding %s to monitor cluster health", jmxName));
    Preferences.clusterHealthProperty().getValue().instances.add(jmxName);
  }

}

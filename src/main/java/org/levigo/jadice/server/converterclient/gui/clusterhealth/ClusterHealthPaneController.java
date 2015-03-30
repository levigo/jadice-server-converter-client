package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.levigo.jadice.server.converterclient.util.UiUtil;


public class ClusterHealthPaneController {
  
  @FXML
  private Button home;

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
  }

}

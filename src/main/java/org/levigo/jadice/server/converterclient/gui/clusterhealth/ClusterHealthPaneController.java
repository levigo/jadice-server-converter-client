package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import org.controlsfx.control.GridView;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.util.UiUtil;


public class ClusterHealthPaneController {

  @FXML
  private Button home;

  @FXML
  private GridView<StatusControl> gridView;

  private final ObservableList<Rule> rules = FXCollections.observableArrayList();

  private final ObservableList<StatusControl> controlElements = FXCollections.observableArrayList();

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    loadRules();
    loadControlElements();
    gridView.setCellFactory(view -> new StatusControlGridCell());
    gridView.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    gridView.setItems(controlElements);

  }

  private void loadRules() {
    // TODO: make them editable
  }

  private void loadControlElements() {
    // TODO: make them editable
  }

}

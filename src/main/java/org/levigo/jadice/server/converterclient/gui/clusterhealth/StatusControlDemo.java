/*
 * Copyright (c) 2013 by Gerrit Grunwald
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.levigo.jadice.server.converterclient.gui.clusterhealth;


import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.controlsfx.control.GridView;


/**
 * Created by User: hansolo Date: 31.08.13 Time: 08:37
 */
public class StatusControlDemo extends Application {
  
  private static final ClusterInstance instance1 = new ClusterInstance("localhost:61619", FXCollections.emptyObservableList());
  private static final StatusControl control1 = new StatusControl(instance1);

  private static final ClusterInstance instance2 = new ClusterInstance("jadice-server:61619", FXCollections.emptyObservableList());
  private static final StatusControl control2 = new StatusControl(instance2);
  
  private final Timer timer = new Timer();

  @Override
  public void init() {
   timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        instance1.update();
        instance2.update();
        
      }
    }, 0, 5000);
  }
  
  @Override
  public void stop() throws Exception {
    timer.cancel();
  }

  @Override
  public void start(Stage stage) {
    final ObservableList<StatusControl> items = FXCollections.observableArrayList(control1, control2);
    GridView<StatusControl> grid = new GridView<>(items);
    grid.setCellFactory(view -> new StatusControlGridCell());
    grid.setCellWidth(350);
    grid.setCellHeight(30);
    grid.setVerticalCellSpacing(5);
    grid.setHorizontalCellSpacing(5);
    final BorderPane root = new BorderPane(grid);
    Scene scene = new Scene(root, 128, 128);
    root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

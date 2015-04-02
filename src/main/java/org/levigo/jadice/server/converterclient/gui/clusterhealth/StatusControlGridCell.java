package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import org.controlsfx.control.GridCell;

public class StatusControlGridCell extends GridCell<StatusControl> {
  
  @Override
  protected void updateItem(StatusControl item, boolean empty) {
    if (empty || item == null) {
      setGraphic(null);
    } else {
      setGraphic(item);
    }
  }
}

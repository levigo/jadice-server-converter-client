package org.levigo.jadice.server.converterclient.gui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.levigo.jadice.server.converterclient.gui.conversion.ConversionPaneController;

public class OSHelper {
  private static final Logger LOGGER = Logger.getLogger(ConversionPaneController.class);

  public static void open(File file) throws IOException {
    if (!isOpenSupported()) {
      return;
    }
    Desktop.getDesktop().open(file);
  }

  public static void openInBackground(File file) {

    Thread backgroundThread = new Thread(() -> {
      try {
        // Run the task in a background thread
        open(file);
      } catch (IOException e) {
        LOGGER.error("Could not open result file", e);
      }
    });
    backgroundThread.setDaemon(true);
    backgroundThread.start();
  }

  public static boolean isOpenSupported() {
    return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN);
  }
}
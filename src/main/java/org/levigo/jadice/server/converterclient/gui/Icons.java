package org.levigo.jadice.server.converterclient.gui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.scene.image.Image;

public class Icons {
  
  private static final Logger LOGGER = Logger.getLogger(Icons.class);
  
  private static final String[] ICON_RESOURCES = new String[] {
    "/icons/jadice_server_16x16.png", //
    "/icons/jadice_server_24x24.png", //
    "/icons/jadice_server_32x32.png", //
    "/icons/jadice_server_48x48.png", //
    "/icons/jadice_server_64x64.png", //
    "/icons/jadice_server_256x256.png" //
  };
  
  private static Image[] icons;
  
  public static Image[] getAllIcons() {
    if (icons == null) {
      icons = loadIcons();
    }
    return icons;
  }
  
  private static Image[] loadIcons() {
    LOGGER.info("Loading window icons");
    List<Image> result = new ArrayList<>();
    for (String s : ICON_RESOURCES) {
      LOGGER.debug("Loading window icon " + s);
      final InputStream is = Icons.class.getResourceAsStream(s);
      if (is == null) {
        LOGGER.error("Could not load icon '" + s  + "'. Resource is not available!");
        continue;
      }
      final Image image = new Image(is);
      if (image.getWidth() == 0 || image.getHeight() == 0) {
        LOGGER.error("Could not load icon '" + s + "'. Resource is not a valid image!");
      }
      LOGGER.debug("Loaded window icon successfully");
      result.add(image);
  }
    return result.toArray(new Image[0]);
  }

}

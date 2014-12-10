package org.levigo.jadice.server.converterclient.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.scene.image.Image;

public class Icons {
  
  private static final Logger LOGGER = Logger.getLogger(Icons.class);
  
  private static final String[] ICON_RESSOURCES = new String[] {
    "/icons/jadice_server_16x16.png", //
    "/icons/jadice_server_24x24.png", //
    "/icons/jadice_server_32x32.png", //
    "/icons/jadice_server_48x48.png", //
    "/icons/jadice_server_64x64.png" //
  };
  
  private static Image[] icons;
  
  public static Image[] getAllIcons() {
    if (icons == null) {
      icons = loadIcons();
    }
    return icons;
  }
  
  private static Image[] loadIcons() {
    LOGGER.debug("Loading window icons");
    List<Image> result = new ArrayList<>();
    for (String s : ICON_RESSOURCES) {
      result.add(new Image(Icons.class.getResourceAsStream(s)));
    }
    return result.toArray(new Image[0]);
  }

}

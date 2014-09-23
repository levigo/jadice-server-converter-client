package org.levigo.jadice.server.converterclient.util;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

import org.levigo.jadice.server.converterclient.gui.ConverterClientApplication;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

public class UiUtil {
  
  private UiUtil() {
    // hidden constr.
  }

  public static void configureHomeButton(Button home) {
    AwesomeDude.setIcon(home, AwesomeIcon.ARROW_LEFT, "15px", ContentDisplay.GRAPHIC_ONLY);
    home.setOnAction(evt -> ConverterClientApplication.getInstance().openMenu());
  }
}

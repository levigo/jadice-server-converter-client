package org.levigo.jadice.server.converterclient.updatecheck;

import java.awt.Desktop;

import javafx.scene.Node;

import org.apache.log4j.Logger;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;

public final class UpdateDialogs {
  
  private static final Logger LOGGER = Logger.getLogger(UpdateDialogs.class);

  private UpdateDialogs() {
    // Utility class -> hidden constr.
  }

  @SuppressWarnings("deprecation")
  public static void showUpdateAvailableDialog(UpdateCheckResult result, Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title("Update Check")//
    .masthead("New version available")//
    .message(String.format("New version available: %s\nYour are working with version %s",//
        result.getLatestVersionNumber(), result.getCurrentVersionNumber()))//
    .actions(new DialogAction("Go to download page", evt2 -> {
      try {
        Desktop.getDesktop().browse(result.getLatestReleaseURL().toURI());
      } catch (Exception e) {
        LOGGER.error("Could not open download page", e);
        showUpdateErrorDialog(e, owner);
      }
    }))//
    .owner(owner)//
    .showInformation();
  }

  @SuppressWarnings("deprecation")
  public static void showNoUpdateAvailableDialog(Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title("Update Check")//
    .message("Your software is up to date")//
    .owner(owner)//
    .showInformation();
  }

  @SuppressWarnings("deprecation")
  public static void showUpdateErrorDialog(Throwable error, Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title("Update Check")//
    .message("Could not perform update check")//
    .owner(owner)//
    .showException(error);
  }

}

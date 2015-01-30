package org.levigo.jadice.server.converterclient.updatecheck;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;

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
    .title(getUiResources().getString("dialogs.update.update-available.title"))//
    .masthead(getUiResources().getString("dialogs.update.update-available.masthead"))//
    .message(String.format(getUiResources().getString("dialogs.update.update-available.message"),//
        result.getLatestVersionNumber(), result.getCurrentVersionNumber()))//
    .actions(new DialogAction(getUiResources().getString("dialogs.update.update-available.action"), evt -> {
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
    .title(getUiResources().getString("dialogs.update.latest-version.title"))//
    .message(getUiResources().getString("dialogs.update.latest-version.message"))//
    .owner(owner)//
    .showInformation();
  }

  @SuppressWarnings("deprecation")
  public static void showUpdateErrorDialog(Throwable error, Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title(getUiResources().getString("dialogs.update.error.title"))//
    .message(getUiResources().getString("dialogs.update.error.message"))//
    .owner(owner)//
    .showException(error);
  }

}

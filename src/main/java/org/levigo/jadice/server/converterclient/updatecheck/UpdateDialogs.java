package org.levigo.jadice.server.converterclient.updatecheck;

import static org.levigo.jadice.server.converterclient.gui.ConverterClientApplication.getI18nResources;

import java.awt.Desktop;

import javafx.scene.Node;

import org.apache.log4j.Logger;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;
import org.levigo.jadice.server.converterclient.gui.ConverterClientApplication;

public final class UpdateDialogs {
  
  private static final Logger LOGGER = Logger.getLogger(UpdateDialogs.class);
  
  private UpdateDialogs() {
    // Utility class -> hidden constr.
  }

  @SuppressWarnings("deprecation")
  public static void showUpdateAvailableDialog(UpdateCheckResult result, Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title(getI18nResources().getString("dialogs.update.update-available.title"))//
    .masthead(getI18nResources().getString("dialogs.update.update-available.masthead"))//
    .message(String.format(getI18nResources().getString("dialogs.update.update-available.message"),//
        result.getLatestVersionNumber(), result.getCurrentVersionNumber()))//
    .actions(new DialogAction(getI18nResources().getString("dialogs.update.update-available.action"), evt -> {
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
    .title(getI18nResources().getString("dialogs.update.latest-version.title"))//
    .message(getI18nResources().getString("dialogs.update.latest-version.message"))//
    .owner(owner)//
    .showInformation();
  }

  @SuppressWarnings("deprecation")
  public static void showUpdateErrorDialog(Throwable error, Node owner) {
    Dialogs.create()//
    .styleClass(Dialog.STYLE_CLASS_NATIVE)//
    .title(getI18nResources().getString("dialogs.update.error.title"))//
    .message(getI18nResources().getString("dialogs.update.error.message"))//
    .owner(owner)//
    .showException(error);
  }

}

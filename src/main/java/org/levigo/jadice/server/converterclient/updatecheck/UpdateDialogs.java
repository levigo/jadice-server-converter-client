package org.levigo.jadice.server.converterclient.updatecheck;

import static org.levigo.jadice.server.converterclient.util.UiUtil.getUiResources;

import java.awt.Desktop;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.controlsfx.dialog.ExceptionDialog;
import org.levigo.jadice.server.converterclient.gui.Icons;

public final class UpdateDialogs {
  
  private static final Logger LOGGER = Logger.getLogger(UpdateDialogs.class);
  
  private UpdateDialogs() {
    // Utility class -> hidden constr.
  }

  public static void showUpdateAvailableDialog(UpdateCheckResult result, Node owner) {
    final String title = getUiResources().getString("dialogs.update.update-available.title");
    final ButtonType downloadBttn = new ButtonType(getUiResources().getString("dialogs.update.update-available.action"));
    
    final Alert dialog = new Alert(AlertType.INFORMATION, title, downloadBttn);
    dialog.setTitle(title);
    dialog.setHeaderText(getUiResources().getString("dialogs.update.update-available.masthead"));
    dialog.setContentText(String.format(getUiResources().getString("dialogs.update.update-available.message"),//
        result.getLatestVersionNumber(), result.getCurrentVersionNumber()));
    dialog.initOwner(owner.getScene().getWindow());
    
    // http://code.makery.ch/blog/javafx-dialogs-official/
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().addAll(Icons.getAllIcons());
    
    final Optional<ButtonType> choice = dialog.showAndWait();
    if (choice.isPresent() && choice.get() == downloadBttn) {
      try {
        Desktop.getDesktop().browse(result.getLatestReleaseURL().toURI());
      } catch (Exception e) {
        LOGGER.error("Could not open download page", e);
        showUpdateErrorDialog(e, owner);
      }
    }
  }

  public static void showNoUpdateAvailableDialog(Node owner) {
    final Alert dialog = new Alert(AlertType.INFORMATION);
    dialog.setTitle(getUiResources().getString("dialogs.update.latest-version.title"));
    dialog.setHeaderText(getUiResources().getString("dialogs.update.latest-version.message"));
    dialog.initOwner(owner.getScene().getWindow());
    
    // http://code.makery.ch/blog/javafx-dialogs-official/
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().addAll(Icons.getAllIcons());

    dialog.show();
  }

  public static void showUpdateErrorDialog(Throwable error, Node owner) {
    final ExceptionDialog dialog = new ExceptionDialog(error);
    dialog.setTitle(getUiResources().getString("dialogs.update.error.title"));
    dialog.setHeaderText(getUiResources().getString("dialogs.update.error.message"));
    dialog.initOwner(owner.getScene().getWindow());
    
    // http://code.makery.ch/blog/javafx-dialogs-official/
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().addAll(Icons.getAllIcons());
    dialog.show();
  }

}

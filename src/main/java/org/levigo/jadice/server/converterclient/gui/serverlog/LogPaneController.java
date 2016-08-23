package org.levigo.jadice.server.converterclient.gui.serverlog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.controlsfx.dialog.ExceptionDialog;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.Icons;
import org.levigo.jadice.server.converterclient.util.LogEvent;
import org.levigo.jadice.server.converterclient.util.LogEventParser;
import org.levigo.jadice.server.converterclient.util.UiUtil;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class LogPaneController implements MessageListener {
  
  private static final Logger LOGGER = Logger.getLogger(LogPaneController.class);
  
  @FXML
  private Button home;
  
  @FXML
  private ComboBox<String> servers;
  
  @FXML
  private Button subscribe;
  
  @FXML
  private Button clear;
  
  @FXML
  private CheckBox scrollLock;
  
  @FXML
  private TableView<LogEvent> logMessages;
  
  @FXML
  private TableColumn<LogEvent, String> timestamp;
  
  @FXML
  private TableColumn<LogEvent, Level> level;
  
  @FXML
  private TableColumn<LogEvent, String> ndc;
  
  @FXML
  private TableColumn<LogEvent, String> logger;
  
  @FXML
  private TableColumn<LogEvent, String> message;
  
  @FXML
  private TableColumn<LogEvent, String> stacktrace;
  
  @FXML
  private ResourceBundle resources;

  ObjectProperty<Subscription> subscription = new SimpleObjectProperty<>();

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);

    logMessages.setItems(FXCollections.observableArrayList());
    timestamp.setCellValueFactory(cell -> 
      new SimpleStringProperty(new SimpleDateFormat(resources.getString("server-log.table.timestamp-format")) //
        .format(cell.getValue().timestampProperty().getValue()))
    );
    
    // TODO: Set text color according to the level
    level.setCellValueFactory(cell -> cell.getValue().levelProperty());
    ndc.setCellValueFactory(cell -> cell.getValue().ndcProperty());
    logger.setCellValueFactory(cell -> cell.getValue().loggerNameProperty());
    message.setCellValueFactory(cell -> cell.getValue().messageProperty());
    stacktrace.setCellValueFactory(cell -> cell.getValue().stacktraceProperty());

    servers.itemsProperty().bind(Preferences.recentServersProperty());
    servers.setValue(servers.getItems().get(0));
    
    subscribe.textProperty().bind(Bindings.when(subscription.isNull()) //
        .then(resources.getString("server-log.subscribe")) //
        .otherwise(resources.getString("server-log.unsubscribe")));
  }
  
  @FXML
  protected void clearLogMessages() {
    logMessages.getItems().clear();
  }

  @FXML
  protected void toggleSubscription() {
    if (subscription.get() == null) {
      try {
        subscription.set(SubscriptionFactory.getInstance().createSubscription(servers.getValue(), this));
      } catch (Exception e) {
        LOGGER.error("Connection Error", e);
        final ExceptionDialog dialog = new ExceptionDialog(e);
        dialog.setTitle(resources.getString("dialogs.server-log.connection-error.title"));
        dialog.setHeaderText(resources.getString("dialogs.server-log.connection-error.message"));
        dialog.initOwner(subscribe.getScene().getWindow());
        
        // http://code.makery.ch/blog/javafx-dialogs-official/
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().addAll(Icons.getAllIcons());
        dialog.show();
      }
    } else {
      try {
        subscription.get().close();
      } catch (Exception e) {
        LOGGER.error("Disconnecting Error", e);
      } finally {
        subscription.set(null);
      }
    }

  }

  @Override
  public void onMessage(Message message) {
    try {
      if (!(message instanceof TextMessage)) {
        LOGGER.warn("Expected a TextMessage but got a " + message.getClass().getSimpleName());
        return;
      }
      
      String raw = ((TextMessage) message).getText();
      LOGGER.debug("Received log event: " + raw);

      final LogEvent logEvent = LogEventParser.getInstance().parse(raw);
      Platform.runLater(() -> {
        logMessages.getItems().add(logEvent);
        if (!scrollLock.isSelected()) {
            logMessages.scrollTo(logEvent);
        }
      });
    } catch (JMSException | IOException e) {
      LOGGER.error("Cannot process message", e);
    }
  }
}
 
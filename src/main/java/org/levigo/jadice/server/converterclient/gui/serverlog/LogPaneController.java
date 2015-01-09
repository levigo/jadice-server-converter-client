package org.levigo.jadice.server.converterclient.gui.serverlog;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.util.UiUtil;

import com.levigo.jadice.server.util.Util;


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
  private TableView<LoggingEvent> logMessages;
  
  @FXML
  private TableColumn<LoggingEvent, String> timestamp;
  
  @FXML
  private TableColumn<LoggingEvent, Level> level;
  
  @FXML
  private TableColumn<LoggingEvent, String> ndc;
  
  @FXML
  private TableColumn<LoggingEvent, String> logger;
  
  @FXML
  private TableColumn<LoggingEvent, Object> message;
  
  @FXML
  private TableColumn<LoggingEvent, String> stacktrace;

  ObjectProperty<Subscription> subscription = new SimpleObjectProperty<>();

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);

    logMessages.setItems(FXCollections.observableArrayList());
    timestamp.setCellValueFactory(cell -> 
      new SimpleStringProperty(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(cell.getValue().getTimeStamp())))
    );
    
    // TODO: Set text color according to the level
    level.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getLevel()));
    ndc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNDC()));
    logger.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLoggerName()));
    message.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getMessage()));
    stacktrace.setCellValueFactory(cell -> {
      final String[] raw = cell.getValue().getThrowableStrRep();
      if (raw == null || raw.length <= 0) {
        return null;
      }
      return new SimpleObjectProperty<>(Util.join(Arrays.asList(raw), "\n"));
    });

    servers.itemsProperty().bind(Preferences.recentServersProperty());
    servers.setValue(servers.getItems().get(0));
    
    subscribe.setOnAction(evt -> {
      if (subscription.get() == null) {
        try {
          subscription.set(SubscriptionFactory.getInstance().createSubscription(servers.getValue(), this));
        } catch (Exception e) {
          LOGGER.error("Connection Error", e);
          Dialogs.create()
            .owner(subscribe)
            .styleClass(Dialog.STYLE_CLASS_NATIVE)
            .title("Connection Error")
            .message("Cannot subscribe to server log")
            .showException(e);
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
    });
    subscribe.textProperty().bind(Bindings.when(subscription.isNull()).then("Subscribe").otherwise("Unsubscribe"));
    
    clear.setOnAction(evt -> {
      logMessages.getItems().clear();
    });
  }

  @Override
  public void onMessage(Message message) {
    try {
      if (!(message instanceof ObjectMessage)) {
        return;
      }
      Object o = ((ObjectMessage) message).getObject();
      if (!(o instanceof LoggingEvent)) {
        return;
      }
      final LoggingEvent logEvent = (LoggingEvent) o;
      Platform.runLater(() -> {
        logMessages.getItems().add(logEvent);
        if (!scrollLock.isSelected()) {
            logMessages.scrollTo(logEvent);
        }
      });
    } catch (JMSException e) {
      LOGGER.error("Cannot process message", e);
    }
  }
}
 
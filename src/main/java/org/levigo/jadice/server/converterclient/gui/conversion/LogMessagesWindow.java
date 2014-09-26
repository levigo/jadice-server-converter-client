package org.levigo.jadice.server.converterclient.gui.conversion;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DateFormat;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.levigo.jadice.server.converterclient.JobCard;
import org.levigo.jadice.server.converterclient.LogMessage;

import com.levigo.jadice.server.Node;

public class LogMessagesWindow {

  private static SoftReference<LogMessagesWindow> INSTANCE;
  
  private final Image windowIcon = new Image(getClass().getResourceAsStream("/jadice-server.png"));

  private final Stage stage;
  
  private final TabPane tabPane;

  private LogMessagesWindow() {
    this.tabPane = new TabPane();
    this.stage = new Stage();
    stage.setTitle("Log Messages");
    stage.setScene(new Scene(tabPane, 800, 600));
    stage.getIcons().add(windowIcon);
    stage.getScene().getStylesheets().add("css/MessageColors.css");

    tabPane.getTabs().addListener((ListChangeListener<Tab>) evt -> {
      if (evt.getList().isEmpty()) {
        stage.close();
      }
    });

    // Remove all tabs when window is closed 
    stage.setOnHidden(event -> {
      tabPane.getTabs().clear();
    });
  }

  public static LogMessagesWindow getInstance() {
    LogMessagesWindow f = (INSTANCE == null) ? null : INSTANCE.get();
    if (f == null) {
      f = new LogMessagesWindow();
      INSTANCE = new SoftReference<LogMessagesWindow>(f);
    }
    return f;
  }
  
  private static class Controller {
    @FXML
    TableView<LogMessage> logTable;
    
    @FXML
    TableColumn<LogMessage, String> timestamp;
    
    @FXML
    TableColumn<LogMessage, LogMessage.Type> type;
    
    @FXML
    TableColumn<LogMessage, String> messageId;
    
    @FXML
    TableColumn<LogMessage, String> node;
    
    @FXML
    TableColumn<LogMessage, String> message;
    
    @FXML
    TableColumn<LogMessage, Throwable> cause;
    
    public void init() {
      timestamp.setCellValueFactory(row -> {
        return new SimpleStringProperty(DateFormat.getDateTimeInstance().format(row.getValue().timestamp));
      });
      type.setCellValueFactory(row -> {
        return new SimpleObjectProperty<>(row.getValue().type);
      });
      type.setCellFactory(param -> {
        return new TableCell<LogMessage, LogMessage.Type>() {
          private String customStyleClass = null;
          
          @Override
          public void updateItem(LogMessage.Type item, boolean empty) {
            setText(empty || item == null ? null : item.name());

            // Remove previous style class 
            if (customStyleClass != null) {
              getStyleClass().removeAll(customStyleClass);
            }
            
            if (!empty && item != null) {
              customStyleClass = item.name();
              if (!getStyleClass().contains(customStyleClass)) {
                getStyleClass().add(customStyleClass);
              }
            } 
          }
        };
      });

      messageId.setCellValueFactory(row -> {
        return new SimpleObjectProperty<>(row.getValue().messageId);
      });
      
      node.setCellValueFactory(row -> {
        final Node node = row.getValue().node;
        return new SimpleStringProperty(node == null ? null : node.getClass().getSimpleName());
      });
      
      message.setCellValueFactory(row -> {
        return new SimpleObjectProperty<>(row.getValue().message);
      });

      cause.setCellValueFactory(row -> {
        return new SimpleObjectProperty<>(row.getValue().cause);
      });
    }
  }

  public void showLogmessages(JobCard jc) {
    // A tab for that job already opened?
    final FilteredList<Tab> existingTabs = tabPane.getTabs().filtered(tab -> tab.getText().equals(jc.job.getUUID()));
    if (existingTabs.isEmpty()) {
      try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LogTablePane.fxml"));
        final Controller controller = new Controller();
        fxmlLoader.setController(controller);
        BorderPane root = fxmlLoader.load();
        
        controller.logTable.setItems(jc.getLogMessages());
        final Tab tab = new Tab(jc.job.getUUID());
        tab.setContent(root);
        controller.init();
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      tabPane.getSelectionModel().select(existingTabs.get(0));
    }
    
    stage.show();
    stage.requestFocus();
  }


}

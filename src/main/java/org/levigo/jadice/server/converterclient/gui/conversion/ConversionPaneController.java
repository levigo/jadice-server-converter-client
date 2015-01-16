package org.levigo.jadice.server.converterclient.gui.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.beans.binding.When;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import org.apache.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.levigo.jadice.server.converterclient.JobCard;
import org.levigo.jadice.server.converterclient.JobCardFactory;
import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;
import org.levigo.jadice.server.converterclient.gui.ConverterClientApplication;
import org.levigo.jadice.server.converterclient.gui.OSHelper;
import org.levigo.jadice.server.converterclient.util.UiUtil;

import com.levigo.jadice.server.Job.State;
import com.levigo.jadice.server.Limit;
import com.levigo.jadice.server.util.Util;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;


public class ConversionPaneController {

  private static final Logger LOGGER = Logger.getLogger(ConversionPaneController.class);

  private static final AwesomeIcon LIMITS_ENABLED_ICON = AwesomeIcon.BELL_ALT;

  private static final AwesomeIcon LIMITS_DISABLED_ICON = AwesomeIcon.BELL_SLASH_ALT;
  
  @FXML
  private BorderPane pane;

  @FXML
  private ComboBox<String> servers;

  @FXML
  private ComboBox<WorkflowConfiguration> configurations;

  @FXML
  private Button startConversion;
  
  @FXML
  private Button applyLimits;

  @FXML
  private TableView<JobCard> jobTable;

  @FXML
  private TableColumn<JobCard, String> jobId;

  @FXML
  private TableColumn<JobCard, ObservableList<File>> jobFiles;

  @FXML
  private TableColumn<JobCard, String> jobWorkflow;

  @FXML
  private TableColumn<JobCard, State> jobState;

  @FXML
  private TableColumn<JobCard, Number> jobWarnings;

  @FXML
  private TableColumn<JobCard, Number> jobErrors;

  @FXML
  private TableColumn<JobCard, String> jobServerInstance;
  
  @FXML
  private Button home;

  @FXML
  private Button abortAll;

  @FXML
  private Button openResults;

  @FXML
  private Button clearFailedJobs;

  @FXML
  private Button clearFinishedJobs;

  @FXML
  private Button openLogMessages;
  
  @FXML
  private ContextMenu tableContextMenu;
  
  private PopOver applyLimitsPopover;
  
  private ApplyLimitsPaneController applyLimitsController;

  // Open subsequent FileChoosers at the last location
  private File lastDir = new File(".");

  @FXML
  protected void initialize() {
    jobTable.getStylesheets().add("css/MessageColors.css");

    UiUtil.configureHomeButton(home);
    initJobStartPanel();
    initApplyLimitsButton();
    initTable();
    initDnD();
  }
  
  @FXML
  protected void startConversion() {
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(lastDir);
    final List<File> selected = chooser.showOpenMultipleDialog(pane.getScene().getWindow());
    if (selected != null) {
      lastDir = selected.get(0).getParentFile();
      try {
        submitJob(selected);
      } catch (Exception e) {
        LOGGER.error("Could not submit job", e);
      }
    }
  }
  
  @FXML
  protected void showApplyLimitsPopover() {
    if (applyLimitsPopover == null) {
      return;
    }
    if (applyLimitsPopover.isShowing()) {
      applyLimitsPopover.hide();
    } else {
      applyLimitsPopover.show(applyLimits);
    }
  }
  
  @FXML
  protected void abortAllJobs() {
    // TODO: Don't run on UI Thread
    for (JobCard jobCard : jobTable.getItems()) {
      jobCard.abortJob();
    }
  }
  
  @FXML
  protected void openSelectedResults() {
    for (JobCard jc : jobTable.getSelectionModel().getSelectedItems()) {
      openResults(jc);
    }
  }
  
  @FXML
  protected void clearFailedJobs() {
    // TODO: Don't run on UI Thread
    final FilteredList<JobCard> toRemove = jobTable.getItems().filtered(jobCard -> jobCard.job.getState() == State.ABORTED || jobCard.job.getState() == State.FAILED);
    toRemove.forEach(it -> {
      final int idx = jobTable.getItems().indexOf(it);
      jobTable.getSelectionModel().clearSelection(idx);
    });
    // Clone list to avoid that we modify the list we are deleting...
    jobTable.getItems().removeAll(toRemove.toArray(new JobCard[0]));
  }
   
  @FXML
  protected void clearFinishedJobs() {
    // TODO: Don't run on UI Thread
    final FilteredList<JobCard> toRemove = jobTable.getItems().filtered(jobCard -> jobCard.job.getState() == State.FINISHED);
    toRemove.forEach(it -> {
      final int idx = jobTable.getItems().indexOf(it);
      jobTable.getSelectionModel().clearSelection(idx);
    });

    // Clone list to avoid that we modify the list we are deleting...
    jobTable.getItems().removeAll(toRemove.toArray(new JobCard[0]));
  }
  
  @FXML
  protected void openLogMessages() {
    jobTable.getSelectionModel().getSelectedItems().forEach(jobCard -> {
      LogMessagesWindow.getInstance().showLogmessages(jobCard);
    });
  }
  
  @FXML
  protected void abortSelectedJobs() {
    jobTable.getSelectionModel().getSelectedItems().filtered(jobCard -> !jobCard.job.getState().isTerminalState()).forEach(jobCard -> {
      jobCard.abortJob();
    });
  }
  
  @FXML
  protected void saveSelectedResults() {
    jobTable.getSelectionModel().getSelectedItems().forEach(jobCard -> {
      saveResult(jobCard);
    });
  }
  
  @FXML
  protected void openSelectedOriginalFiles() {
    jobTable.getSelectionModel().getSelectedItems().forEach(jobCard -> {
      openOriginal(jobCard);
    });
  }
  
  @FXML
  protected void retrySelectedJobs() {
    jobTable.getSelectionModel().getSelectedItems().forEach(jobCard -> {
    try {
      JobCardFactory.getInstance().cloneAndSubmitJob(jobCard, servers.getValue(), buildJobLimits());
    } catch (Exception e) {
      LOGGER.error("Could not re-submit job", e);
    }
    });
  }
  
  @FXML
  protected void inspectSelectedWorkflow() {
    final JobCard jobCard = jobTable.getSelectionModel().getSelectedItem();
    if (jobCard != null) {
      ConverterClientApplication.getInstance().openInspector(jobCard);
    }
  }
  
  @FXML
  protected void removeSelectedJobs() {
    // TODO: Don't run on UI Thread
    final FilteredList<JobCard> toRemove = jobTable.getSelectionModel().getSelectedItems().filtered(jobCard -> jobCard.job.getState().isTerminalState());
    toRemove.forEach(it -> {
      final int idx = jobTable.getItems().indexOf(it);
      jobTable.getSelectionModel().clearSelection(idx);
    });
    
    // Clone list to avoid that we modify the list we are deleting...
    jobTable.getItems().removeAll(toRemove.toArray(new JobCard[0]));
  }

  private void initDnD() {
    pane.setOnDragOver(event -> {
      if (event.getDragboard().hasFiles() && event.getGestureSource() != jobTable) {
        event.acceptTransferModes(TransferMode.ANY);
      }
      event.consume();
    });

    pane.setOnDragDropped(event -> {
      final Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles() && event.getGestureSource() != jobTable) {
        try {
          submitJob(event.getDragboard().getFiles());
          success = true;
        } catch (Exception e) {
          LOGGER.warn("Cannot create jobs on Drag&Drop", e);
        }
      }
      event.setDropCompleted(success);
      event.consume();
    });
    
    jobTable.setOnDragDetected(event -> {
      if (jobTable.getSelectionModel().isEmpty()) {
        return;
      }
      
      // Copy all result files from succeeded jobs in table selection:
      final List<File> files = new ArrayList<>();
      jobTable.getSelectionModel().getSelectedItems()
          .filtered(jobCard -> jobCard.job.getState() == State.FINISHED)
          .forEach(jobCard -> files.addAll(jobCard.getResults()));
      if (files.isEmpty()) {
        return;
      }

      final Dragboard db = jobTable.startDragAndDrop(TransferMode.COPY);
      ClipboardContent content = new ClipboardContent();
      content.putFiles(files);
      db.setContent(content);
      event.consume();
    });
  }


  private void initJobStartPanel() {
    servers.itemsProperty().bind(Preferences.recentServersProperty());
    servers.setValue(servers.getItems().get(0));

    configurations.itemsProperty().bind(new SimpleListProperty<>(JobCardFactory.getInstance().getConfigurations()));
    final StringConverter<WorkflowConfiguration> sc = new StringConverter<WorkflowConfiguration>() {
      @Override
      public String toString(WorkflowConfiguration object) {
        return String.format("%s [%s]", object.getDescription(), object.getID());
      }

      @Override
      public WorkflowConfiguration fromString(String string) {
        return null;
      }
    };
    configurations.setCellFactory(ComboBoxListCell.forListView(sc, configurations.getItems()));
    configurations.setButtonCell(new ComboBoxListCell<>(sc, configurations.getItems()));
    configurations.setValue(configurations.getItems().get(0));
  }

  private void initApplyLimitsButton() {
    Node limits = null;
    try {
      final FXMLLoader loader = new FXMLLoader();
      limits = loader.load(getClass().getResourceAsStream("/fxml/ApplyLimitsPane.fxml"));
      applyLimitsController = loader.getController();
    } catch (IOException e) {
      LOGGER.error("Could not load limits pane", e);
      ((FlowPane) applyLimits.getParent()).getChildren().remove(applyLimits);
      return;
    }
    
    applyLimitsPopover = new PopOver(limits);
    applyLimitsPopover.setHideOnEscape(true);
    applyLimitsPopover.setAutoHide(true);
    applyLimitsPopover.setDetachable(false);
    applyLimitsPopover.setArrowLocation(ArrowLocation.TOP_RIGHT);

    final Label enabledIcon = AwesomeDude.createIconLabel(LIMITS_ENABLED_ICON);
    final Label disabledIcon = AwesomeDude.createIconLabel(LIMITS_DISABLED_ICON);
    final ReadOnlyBooleanProperty emptyLimitsProperty = new ReadOnlyListWrapper<Limit>(applyLimitsController.getLimits()).emptyProperty();
    applyLimits.graphicProperty().bind(new When(emptyLimitsProperty).then(disabledIcon).otherwise(enabledIcon));
  }

  private void initTable() {
    jobTable.setItems(FXCollections.observableArrayList());
    jobTable.selectionModelProperty().get().setSelectionMode(SelectionMode.MULTIPLE);
    JobCardFactory.getInstance().addListener(newCard -> {
      jobTable.getItems().addAll(newCard);
    });
    
    jobId.setCellValueFactory(cell ->  new SimpleStringProperty(cell.getValue().job.getUUID()));
    jobFiles.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().files));
    jobWorkflow.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().config.getID()));
    jobState.setCellValueFactory(cell -> cell.getValue().jobStateProperty);
    jobState.setCellFactory(param -> 
      new TableCell<JobCard, State>() {
        String customStyle = null;
        
        @Override
        public void updateItem(State item, boolean empty) {
          setText(empty || item == null ? null : item.name());

          // Undo any previously made style changes
          if (customStyle != null) {
            getStyleClass().remove(customStyle);
            customStyle = null;
          }
            
          if (!empty && item != null) {
            customStyle = item.name();
            getStyleClass().add(customStyle);
          }
       }
      }
    );
    
    jobWarnings.setCellValueFactory(cell -> cell.getValue().warningCount);
    jobWarnings.setCellFactory(param ->
      new TableCell<JobCard, Number>() {
        private final static String CUSTOM_STYLE_CLASS = "WARNING_BACKGROUND";
        
        @Override
        public void updateItem(Number item, boolean empty) {
          setText(empty || item == null ? null : Integer.toString(item.intValue()));

          if (!empty && item.intValue() > 0) {
            if (!getStyleClass().contains(CUSTOM_STYLE_CLASS)) {
              getStyleClass().add(CUSTOM_STYLE_CLASS);
            }
          } else {
            getStyleClass().removeAll(CUSTOM_STYLE_CLASS);
          }
        }
      }
    );

    jobErrors.setCellValueFactory(cell -> cell.getValue().errorCount);
    jobErrors.setCellFactory(param -> 
      new TableCell<JobCard, Number>() {
        private final static String CUSTOM_STYLE_CLASS = "ERROR_BACKGROUND";
        
        @Override
        public void updateItem(Number item, boolean empty) {
          setText(empty || item == null ? null : Integer.toString(item.intValue()));
          if (!empty && item.intValue() > 0) {
            if (!getStyleClass().contains(CUSTOM_STYLE_CLASS)) {
              getStyleClass().add(CUSTOM_STYLE_CLASS);
            }
          } else {
            getStyleClass().removeAll(CUSTOM_STYLE_CLASS);
          }
        }
      }
    );

    jobServerInstance.setCellValueFactory(cell -> cell.getValue().serverInstanceNameProperty);

    // only display context menu for non-null items:
    final ReadOnlyBooleanProperty emptyLimitsProperty = new ReadOnlyListWrapper<JobCard>(jobTable.getSelectionModel().getSelectedItems()).emptyProperty();
    jobTable.contextMenuProperty().bind(new When(emptyLimitsProperty.not()).then(tableContextMenu).otherwise((ContextMenu) null));
    
    jobTable.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case ESCAPE :
          // Abort Job
          for (JobCard jc : jobTable.getSelectionModel().getSelectedItems()) {
            jc.abortJob();
          }
          event.consume();
          break;
          
        case F5 :
          // Retry
          for (JobCard jc : jobTable.getSelectionModel().getSelectedItems()) {
            try {
              JobCardFactory.getInstance().cloneAndSubmitJob(jc, servers.getValue(), buildJobLimits());
            } catch (Exception e) {
              LOGGER.error("Could not re-submit job", e);
            }
          }
          event.consume();
          break;
          
        case ENTER :
          // Open Result
          for (JobCard jc : jobTable.getSelectionModel().getSelectedItems()) {
            jc.getResults().stream().forEach(file -> {
              try {
                OSHelper.open(file);
              } catch (IOException e) {
                LOGGER.error("Could not open result file", e);
              }
            });
          }
          event.consume();
          break;
          
        case DELETE :
          // remove selected
          final FilteredList<JobCard> toRemove = jobTable.getItems().filtered(item -> //
            jobTable.getSelectionModel().getSelectedItems().contains(item)
              && item.jobStateProperty.get().isTerminalState());
          // Clone list to avoid that we modify the list we are deleting...
          jobTable.getItems().removeAll(toRemove.toArray(new JobCard[0]));
          jobTable.getSelectionModel().clearSelection();
          event.consume();
          break;

        default :
          break;
      }
      
    });

    jobTable.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        openSelectedResults();
        event.consume();
      }
    });

  }

  public void openResults(JobCard job) {
    job.getResults().forEach(file -> {
      try {
        OSHelper.open(file);
      } catch (IOException e) {
        LOGGER.error("Could not open result file", e);
      }
    });
  }

  public void openOriginal(JobCard job) {
    for (File file : job.files) {
      try {
        OSHelper.open(file);
      } catch (IOException e) {
        LOGGER.error("Could not open original file", e);
      }
    }
  }

  public void submitJob(File file) throws Exception {
    if (file.isFile() && file.canRead()) {
      JobCardFactory.getInstance().createAndSubmitJobCard(file, servers.getValue(), configurations.getValue(), buildJobLimits());
    } else if (file.isDirectory()) {
      final File[] files = file.listFiles();
      if (files == null || files.length == 0) {
        return;
      }
      submitJob(Arrays.asList(files));
    }
  }

  private Collection<Limit> buildJobLimits() {
    return applyLimitsController == null ? Collections.emptySet() : applyLimitsController.getLimits();
  }


  public void submitJob(Collection<File> files) throws Exception {
    for (File file : files) {
      submitJob(file);
    }
  }

  private File lastSaveDir = null;

  private void saveResult(JobCard item) {
    item.getResults().forEach(file -> {
      final FileChooser fc = new FileChooser();
      fc.setInitialDirectory(lastSaveDir);
      fc.setInitialFileName(file.getName());
      final File resultFile = fc.showSaveDialog(pane.getScene().getWindow());
      if (resultFile != null) {
        lastSaveDir = resultFile.getParentFile();
        try {
          LOGGER.info("Save as " + resultFile.getAbsolutePath());
          Util.copyAndClose(new FileInputStream(file), new FileOutputStream(resultFile));
        } catch (IOException e) {
          LOGGER.error("Could not save " + resultFile.getName(), e);
          Dialogs.create()
            .owner(pane)
            .styleClass(Dialog.STYLE_CLASS_NATIVE)
            .title("Error")
            .message("Could not save file as " + resultFile.getAbsolutePath())
            .showException(e);
        }
      }
    });
  }
}

package org.levigo.jadice.server.converterclient.gui.options;

import java.io.File;
import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.Preferences.UpdatePolicy;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateCheckResult;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateDialogs;
import org.levigo.jadice.server.converterclient.updatecheck.UpdateService;
import org.levigo.jadice.server.converterclient.util.FilenameGenerator;
import org.levigo.jadice.server.converterclient.util.UiUtil;


public class OptionsPane extends BorderPane {
  
  @FXML
  private Button home;
  
  @FXML
  private TextField jmsUsername;
  
  @FXML
  private PasswordField jmsPassword;
  
  @FXML
  private TextField jmsRequestQueue;
  
  @FXML
  private TextField jmsLogTopic;
  
  @FXML
  private Slider jmsJobPriority;

  @FXML
  private CheckBox cacheJobFactory;
  
  
  @FXML
  private TextField resultFolder;
  
  @FXML
  private Slider concurrentJobs; 
  
  @FXML
  private TextField jmxUsername;
  
  @FXML
  private TextField jmxPassword;
  
  
  @FXML
  private TextField defaultExtension;
  
  @FXML
  private TextField resultFilename;
  
  @FXML
  private Text patternExplanation;
  
  
  @FXML
  private Button changeResultsFolder;
  
  @FXML
  private Button clearServerHistory;
  
  @FXML
  private RadioButton checkUpdatesOnStart;
  
  @FXML
  private RadioButton neverCheckUpdates;
  
  @FXML
  private Button checkUpdatesNow;
  
  @FXML
  private ToggleGroup updateCheckGroup;
  
  @FXML
  private Button restoreDefaults;
  
  public OptionsPane() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/OptionsPane.fxml"));

    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    UiUtil.configureHomeButton(home);
    initValueBindings();
    initButtonBindings();
  }
  
  private void initButtonBindings() {
    restoreDefaults.setOnAction(event -> {
      Preferences.restoreDefaults();
    });
    
    clearServerHistory.setOnAction(event -> {
      Preferences.recentServersProperty().clear();
      Preferences.recentJmxUrlsProperty().clear();
    });
    
    changeResultsFolder.setOnAction(event -> {
      final DirectoryChooser dirChooser = new DirectoryChooser();
      File f = new File(resultFolder.textProperty().getValue());
      if (f.exists() && f.isDirectory()) {
        dirChooser.setInitialDirectory(f);
      }
      final File selectedDirectory = dirChooser.showDialog(getScene().getWindow());
      if (selectedDirectory != null) {
        Preferences.resultFolderProperty().setValue(selectedDirectory);
      }
    });
    
    checkUpdatesNow.setOnAction(event -> {
      final UpdateService updateService = UpdateService.getInstance();
      updateService.setOnSucceeded(evt -> {
        final UpdateCheckResult result = updateService.getValue();
        if (result.isNewerVersionAvailable()) {
          UpdateDialogs.showUpdateAvailableDialog(result);
        } else {
          UpdateDialogs.showNoUpdateAvailableDialog();
        }
      });
      updateService.setOnFailed(evt -> {
        UpdateDialogs.showUpdateErrorDialog(updateService.getException());
      });
      updateService.restart();
    });
  }

  private void initValueBindings() {
    // JMS Properties
    jmsUsername.textProperty().bindBidirectional(Preferences.jmsUsernameProperty());
    jmsPassword.textProperty().bindBidirectional(Preferences.jmsPasswordProperty());
    jmsRequestQueue.textProperty().bindBidirectional(Preferences.jmsRequestQueueNameProperty());
    jmsLogTopic.textProperty().bindBidirectional(Preferences.jmsLogTopicNameProperty());
    jmsJobPriority.valueProperty().bindBidirectional(Preferences.jmsJobPriority());
    cacheJobFactory.selectedProperty().bindBidirectional(Preferences.cacheJmsJobFactoryProperty());

    // JMX Settings
    jmxUsername.textProperty().bindBidirectional(Preferences.jmxUsernameProperty());
    jmxPassword.textProperty().bindBidirectional(Preferences.jmxPasswordProperty());
    
    // Misc.
    concurrentJobs.valueProperty().bindBidirectional(Preferences.concurrentJobsProperty());
    // Read-only, so uni-directional binding is ok
    resultFolder.textProperty().bind(Bindings.convert(Preferences.resultFolderProperty()));
    defaultExtension.textProperty().bindBidirectional(Preferences.defaultExtensionProperty());
    resultFilename.textProperty().bindBidirectional(Preferences.resultFilenamePatternProperty());
    patternExplanation.textProperty().setValue(FilenameGenerator.buildExplanationText(" / "));
    
    
    // javaFX has no smart binding between Enumeration and RadioButtons (yet?)
    setUpdateButtons(Preferences.updatePolicyProperty().getValue());
    Preferences.updatePolicyProperty().addListener((bean, oldValue, newValue) -> {
      if (oldValue == newValue) {
        return;
      }
      setUpdateButtons(newValue);
    });
    updateCheckGroup.selectedToggleProperty().addListener((bean, oldValue, newValue) -> {
      UpdatePolicy newPolicy = null;
      if (checkUpdatesOnStart.isSelected()) {
        newPolicy = UpdatePolicy.ON_EVERY_START;
      } else if (neverCheckUpdates.isSelected()) {
        newPolicy = UpdatePolicy.NEVER;
      }
      
      Preferences.updatePolicyProperty().setValue(newPolicy);
    });
  }

  private void setUpdateButtons(UpdatePolicy policy) {
    checkUpdatesOnStart.setSelected(policy == UpdatePolicy.ON_EVERY_START);
    neverCheckUpdates.setSelected(policy == UpdatePolicy.NEVER);
  }

} 
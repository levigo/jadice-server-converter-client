package org.levigo.jadice.server.converterclient.gui.about;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;

import org.levigo.jadice.server.converterclient.util.Log4JForwarder;
import org.levigo.jadice.server.converterclient.util.UiUtil;


public class AboutPaneController {
  
  @FXML
  private Button home;

  @FXML
  private WebView aboutWebView;

  @FXML
  private WebView licenseWebView;

  @FXML
  private WebView thirdPartyWebView;

  @FXML
  private TextArea logView;

  @FXML
  protected void initialize() {
    UiUtil.configureHomeButton(home);
    loadLicenses();
    initLogView();
  }

  private void loadLicenses() {
    loadLicense(aboutWebView, "/about.html");
    loadLicense(licenseWebView, "/licenses/levigo_bsd.html");
    loadLicense(thirdPartyWebView, "/licenses/3rd_party.html");
  }

  private void loadLicense(WebView wv, String uri) {
    wv.getEngine().load(getClass().getResource(uri).toExternalForm());
  }

  private void initLogView() {
    Log4JForwarder.getInstance().setLogHandler(message -> {
      Platform.runLater(() -> {
        if (logView.getLength() == 0) {
          logView.setText(message);
        } else {
          logView.selectEnd();
          logView.insertText(logView.getText().length(), message);
        }
        // TODO: truncate log view to a maximum nmbr of lines
      });
    });
  }
}

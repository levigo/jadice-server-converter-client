package org.levigo.jadice.server.converterclient.updatecheck;

import java.net.URL;

public interface UpdateCheckResult {
  
  boolean isNewerVersionAvailable();
  
  String getLatestVersionNumber();
  
  String getCurrentVersionNumber();

  URL getLatestReleaseURL();
  
  URL getLatestDownloadURL();

}

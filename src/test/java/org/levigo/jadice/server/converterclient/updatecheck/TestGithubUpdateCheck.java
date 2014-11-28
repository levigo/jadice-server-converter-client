package org.levigo.jadice.server.converterclient.updatecheck;

import org.apache.log4j.Logger;

public class TestGithubUpdateCheck {

  private static final Logger LOGGER = Logger.getLogger(TestGithubUpdateCheck.class);
  
  public static void main(String[] args) throws Exception {
    final GithubUpdateCheck check = new GithubUpdateCheck();
    check.checkForUpdates();
    LOGGER.info("current version number: " + check.getCurrentVersionNumber());
    LOGGER.info("latest version number: " + check.getLatestVersionNumber());
    LOGGER.info("newer version available? " + check.isNewerVersionAvailable());
    LOGGER.info("release notes: " + check.getLatestReleaseNotes());
    LOGGER.info("download URL: " + check.getLatestDownloadURL());
    LOGGER.info("description URL: " + check.getLatestReleaseURL());
  }
}

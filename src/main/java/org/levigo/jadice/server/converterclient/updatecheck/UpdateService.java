package org.levigo.jadice.server.converterclient.updatecheck;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateService extends Service<UpdateCheckResult> {

  private static UpdateService INSTANCE;
  
  public static UpdateService getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new UpdateService();
    }
    return INSTANCE;
  }

  @Override
  protected Task<UpdateCheckResult> createTask() {
    return new Task<UpdateCheckResult>() {

      @Override
      protected UpdateCheckResult call() throws Exception {
        final GithubUpdateCheck check = new GithubUpdateCheck();
        check.checkForUpdates();
        return check;
      }
    };
  }
}

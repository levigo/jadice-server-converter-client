package org.levigo.jadice.server.converterclient.gui.inspector;

import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;

import com.levigo.jadice.server.ConfigurationException;
import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.JobException;
import com.levigo.jadice.server.Node;

/**
 * Dummy {@link Job} implementation that cannot work, but is to introspect a
 * {@link WorkflowConfiguration}.
 */
public class DummyJob extends Job {

  private static final String MSG = "This is just a dummy implementation";

  @Override
  public void attach(Node node) {
    super.node = node;
  }

  @Override
  public void abort() throws JobException {
    throw new JobException(MSG);
  }

  @Override
  public void submit() throws JobException {
    throw new JobException(MSG);
  }

  @Override
  public void close() throws JobException {
    // Nothing to do
  }

  @Override
  protected void validateConfiguration() throws ConfigurationException {
    // Nothing to do
  }

}

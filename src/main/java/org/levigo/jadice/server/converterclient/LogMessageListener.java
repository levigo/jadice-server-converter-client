package org.levigo.jadice.server.converterclient;

public interface LogMessageListener {

	public void logMessageAdded(LogMessage message, JobCard card);
}


package org.levigo.jadice.server.converterclient;

@FunctionalInterface
public interface JobCardListener {
	public void jobCardCreated(JobCard jobCard);
}

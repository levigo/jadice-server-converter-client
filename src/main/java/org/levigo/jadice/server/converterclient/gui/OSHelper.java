package org.levigo.jadice.server.converterclient.gui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;

public class OSHelper {

	public static void open(File file) throws IOException {
	  if (!isOpenSupported()) {
	    return;
	  }
	  Desktop.getDesktop().open(file);
	}
	
	public static boolean isOpenSupported() {
	  return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN);
	}
}
package org.levigo.jadice.server.converterclient.gui.inspector;

import com.levigo.jadice.server.Node;

public interface NodeSelectionListener {

	public void selected(Node selected);

	public void deselected(Node deselected);
}

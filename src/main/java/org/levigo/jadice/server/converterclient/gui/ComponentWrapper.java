package org.levigo.jadice.server.converterclient.gui;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import javafx.embed.swing.SwingNode;

public class ComponentWrapper<C extends JComponent> extends SwingNode {
  
  private final C swing;
  
  @SuppressWarnings("deprecation")
  public ComponentWrapper(C swing) {
    this.swing = swing;

    // Call on Event Dispatch Thread
    SwingUtilities.invokeLater(() -> {
      setContent(swing);
    });
    this.visibleProperty().addListener(evt -> {
      if (this.isVisible()) {
        // Layout breakes when Component is not visible, but resized.
        // -> re-layout the hard way
        SwingUtilities.invokeLater(()-> {
          swing.invalidate();
          swing.layout();
          swing.validate();
          swing.revalidate();
          swing.updateUI();
        });
      }
    });
  }
  
  public C getSwing() {
    return swing;
  }


}

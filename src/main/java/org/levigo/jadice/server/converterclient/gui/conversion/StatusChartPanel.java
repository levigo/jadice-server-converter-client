package org.levigo.jadice.server.converterclient.gui.conversion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.levigo.jadice.server.Job.State;

public class StatusChartPanel extends JPanel implements TableModelListener {
  
  private static final long serialVersionUID = 1478417372829738443L;
  

  public StatusChartPanel() {
  }

  public void tableChanged(TableModelEvent event) {
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
        StatusChartPanel.this.repaint();
//      }});
  }
  
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.LIGHT_GRAY);
    ((Graphics2D)g).draw3DRect(0, 0, getWidth()-1, getHeight()-1, true);
    // FIXME
    final Map<State, Integer> statistics = new HashMap<>();
    int total = 0;
    for (Integer i : statistics.values()) {
      if (i == null)
        continue;
      total += i;
    }
    
    int maxWidth = getWidth() -2;
    int height = getHeight() -2; 
    
    double x = 1.0;
    final State[] states = State.values();
    for (int i = states.length -1 ; i >= 0; i--) {
      State state = states[i];
      final Integer value = statistics.get(state);
      if (value == null || value == 0) {
        continue;
      }
      double width = (double) maxWidth *  ((double)value / (double)total);
//      g.setColor(StateColors.get(state)<);
      g.fillRect((int)Math.floor(x), 1, (int)Math.floor(width), height);
      x += width;
    }
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(200, new JButton("DUMMY").getPreferredSize().height);
  }

}

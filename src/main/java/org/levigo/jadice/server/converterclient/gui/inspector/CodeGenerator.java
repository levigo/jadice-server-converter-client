package org.levigo.jadice.server.converterclient.gui.inspector;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.Node;
import com.levigo.jadice.server.util.NodeTraversal;
import com.levigo.jadice.server.util.NodeVisitor;

public class CodeGenerator {

  @SuppressWarnings("unchecked")
  public static String exportJavaImplementation(Job job) {

    final Map<Node, Integer> nodes = new HashMap<Node, Integer>();
    final Map<Class<Node>, Integer> nodePerClassCount = new HashMap<Class<Node>, Integer>();

    NodeTraversal.traverse(job.getNode(), new NodeVisitor<RuntimeException>() {
      public void visit(Node node) {
        Class<Node> clazz = (Class<Node>) node.getClass();
        int clazzCount = nodePerClassCount.containsKey(clazz) ? nodePerClassCount.get(clazz)+1 : 0;
        nodePerClassCount.put(clazz, clazzCount);
        nodes.put(node, clazzCount);
      }
    }, false);

    StringBuilder sb = new StringBuilder();

    appendLine(sb, "Job j = ...;");
    newline(sb);
    appendLine(sb, "// Node Definitions");

    for (Entry<Node, Integer> node : nodes.entrySet()) {
      final Node node0 = node.getKey();
      appendLine(sb, node0.getClass().getSimpleName() + " " + generateVariableName(node0, nodePerClassCount, nodes) + " = new "
          + node0.getClass().getCanonicalName() + "();");
    }

    newline(sb);
    appendLine(sb, "// Workflow Definition");
    for (Entry<Node, Integer> node : nodes.entrySet()) {
      for (Node succ : node.getKey().getSuccessors()) {
        appendLine(sb, generateVariableName(node.getKey(), nodePerClassCount, nodes) + ".appendSuccesor("
            + generateVariableName(succ, nodePerClassCount, nodes) + ");");
      }
    }
    appendLine(sb, "j.attach(" + generateVariableName(job.getNode(), nodePerClassCount, nodes) + ");");
    appendLine(sb, "j.submit();");


    JFrame f = new JFrame();
    f.setLayout(new BorderLayout());
    final JTextPane editor = new JTextPane();
    editor.setText(sb.toString());
    f.add(editor, BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
    return sb.toString();

  }

  private static String generateVariableName(Node node0, Map<Class<Node>, Integer> nodePerClassCount,
      Map<Node, Integer> nodes) {
    Class<? extends Node> clazz = node0.getClass();
    final String clazzName = clazz.getSimpleName();
    String count = "";
    if (nodePerClassCount.containsKey(clazz)) {
      final Integer value = nodePerClassCount.get(clazz);
      if (value > 0) {
        count = nodes.get(node0).toString();
      }
    }
    
    return clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1) + count;
  }

  private static void appendLine(StringBuilder sb, String text) {
    sb.append(text);
    newline(sb);
  }

  private static void newline(StringBuilder sb) {
    sb.append("\n");
  }
}

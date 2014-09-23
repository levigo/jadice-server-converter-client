package lookup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lookup.NodeLookup.NodeDescriptor;

import com.levigo.jadice.server.Node.Cardinality;

public class NodeView extends Application {
  
  private final static Image PACKAGE_ICON =  new Image("/package.png");
  
//  private final static Image NONE_NONE_ICON = new Image("/none-none.png");
//  
  private final static Image NONE_ONE_ICON = new Image("/none-one.png");
  
//  private final static Image NONE_MANY_ICON = new Image("/none-many.png");
//  
  private final static Image ONE_NONE_ICON = new Image("/one-none.png");
  
  private final static Image ONE_ONE_ICON = new Image("/one-one.png");
  
  private final static Image ONE_MANY_ICON = new Image("/one-many.png");
  
//  private final static Image MANY_NONE_ICON = new Image("/many-none.png");
//  
  private final static Image MANY_ONE_ICON = new Image("/many-one.png");
  
//  private final static Image MANY_MANY_ICON = new Image("/many-many.png");
//  
  private final static Image NODE_ICON =  new Image(NodeView.class.getResourceAsStream("/icons/16x16/actions/recur.png"));
  
  private static class NodeItem extends TreeItem<String> {
    
    public final NodeDescriptor node;
    
    public NodeItem(NodeDescriptor node) {
      super(node.nodeClass.getSimpleName(), buildGraphic(node.inputCardinality, node.outputCardinality));
      this.node = node;
      }
    
    private static ImageView buildGraphic(Cardinality in, Cardinality out) {
      switch (in){
        case NONE :
          switch (out) {
//            case NONE :
//              return new ImageView(NONE_NONE_ICON);
            case ONE :
              return new ImageView(NONE_ONE_ICON);
//            case MANY :
//              return new ImageView(NONE_MANY_ICON);
            default :
              return new ImageView(NODE_ICON);
          }

        case ONE :
          switch (out) {
            case NONE :
              return new ImageView(ONE_NONE_ICON);
            case ONE :
              return new ImageView(ONE_ONE_ICON);
            case MANY :
              return new ImageView(ONE_MANY_ICON);
            default :
              return new ImageView(NODE_ICON);
          }
          
        case MANY :
          switch (out) {
//            case NONE :
//              return new ImageView(MANY_NONE_ICON);
            case ONE :
              return new ImageView(MANY_ONE_ICON);
//            case MANY :
//              return new ImageView(MANY_MANY_ICON);
            default :
              return new ImageView(NODE_ICON);
          }
          
          

        default :
          return new ImageView(NODE_ICON);
      }
    }
    
  }
  
  private static class PackageItem extends TreeItem<String> {
    
    public PackageItem(String packageName) {
      super(packageName, new ImageView(PACKAGE_ICON));
    }
    
  }
  
  private TreeItem<String> root = new PackageItem("(root)");

  private final Map<String, PackageItem> packageItems = new HashMap<>();

  private static Set<NodeDescriptor> nodes;

  public static void main(String[] args) {
    nodes = new NodeLookup().lookupNodes();
    System.out.println("I have nodes");
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Tree View Sample");

    root.setExpanded(true);
    nodes.forEach(it -> {
      NodeItem item = new NodeItem(it);
      final ObservableList<TreeItem<String>> children = findPackageItem(it.nodeClass.getPackage().getName()).getChildren();
      children.add(item);
    });

//    PackageItem root2 = new PackageItem(null);
//    root2.setExpanded(true);
    root = simplifyTree(root);
//    for (TreeItem<String> it : tempRoot.getChildren()) {
//      final TreeItem<String> simply = simplifyTree(it);
//      simply.setExpanded(true);
//      root2.getChildren().add(simply);
//    }

    TreeView<String> tree = new TreeView<>(root);
    root.setExpanded(true);
    tree.setShowRoot(true);
    StackPane root = new StackPane();
    root.getChildren().add(tree);
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }

  private TreeItem<String> simplifyTree(TreeItem<String> item) {
    List<TreeItem<String>> betterChildren = new ArrayList<>();
    for (TreeItem<String> child : item.getChildren()) {
      betterChildren.add(simplifyTree(child));
    }
    item.getChildren().clear();
    betterChildren.sort((TreeItem<String> o1, TreeItem<String> o2) -> {
        return o1.getValue().compareTo(o2.getValue());
      }
    );
    item.getChildren().addAll(betterChildren);
    final boolean isOnePackage = item.getChildren().size() == 1 && item.getChildren().get(0) instanceof PackageItem;
    return isOnePackage ? item.getChildren().get(0) : item;
  }

  private TreeItem<String> findPackageItem(String pk) {
    if (pk == null) {
      return root;
    }

    PackageItem item = packageItems.get(pk);
    if (item == null) {
      item = new PackageItem(pk);
      packageItems.put(pk, item);
      final String parentPk = getParentPackageName(pk);
      System.out.println(pk + " -> " + (parentPk == null ? "root" : parentPk));
      TreeItem<String> parent = findPackageItem(parentPk);
      parent.getChildren().add(item);
    }

    return item;
  }

  private String getParentPackageName(String pk) {
    if (pk == null) {
      return null;
    }
    final int lastDot = pk.lastIndexOf('.');
    if (lastDot == -1) {
      return null;
    }
    return pk.substring(0, lastDot);
  }


}

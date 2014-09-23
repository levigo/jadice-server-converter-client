package lookup;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.levigo.jadice.server.Node;
import com.levigo.jadice.server.Node.Cardinality;


public class NodeLookup {
  
  public static final class NodeDescriptor {
    
    public final Class<Node> nodeClass;
    
    public final Cardinality inputCardinality;
    
    public final Cardinality outputCardinality;
    
    public NodeDescriptor(Class<Node> nodeClass) throws Exception {
      this.nodeClass = nodeClass;
      final Node node = nodeClass.newInstance();
      inputCardinality = node.getInputCardinality();
      outputCardinality = node.getOutputCardinality();
    }
  }

  private Set<NodeDescriptor> nodeClasses;

  final private Set<Package> packages = new HashSet<>();

  public static void main(String[] args) throws Exception {
    final NodeLookup nl = new NodeLookup();

    final Set<NodeDescriptor> nodes = nl.lookupNodes();
    
  }
  
  public NodeLookup() {
    // Run once through the whole JVM class path to prune packages
    lockupNodes(null);
    
    final Set<Package> packages = Arrays.asList(Package.getPackages()).stream().parallel()//
        .filter(it -> !it.getName().startsWith("com.sun."))//
        .filter(it -> !it.getName().startsWith("sun."))//
        .filter(it -> !it.getName().startsWith("java."))//
        .filter(it -> !it.getName().startsWith("javax."))//
        .filter(it -> !it.getName().startsWith("javafx."))//
        .collect(Collectors.toSet());

    prune(Collections.singleton(Package.getPackage("com.levigo.jadice.server")));
    prune(packages);
  }

  public void prune(Set<Package> packages) {
    this.packages.addAll(packages);
  }

  public Set<NodeDescriptor> lookupNodes() {
    if (nodeClasses == null) {
      nodeClasses = lockupNodes(null);
    }
    if (!packages.isEmpty()) {
      packages.stream().parallel()//
          .map(NodeLookup::lockupNodes)//
          .forEach(it -> it.forEach(it2 -> nodeClasses.add(it2)));
      packages.clear();
    }
    return nodeClasses;
  }

  @SuppressWarnings("unchecked")
  private static Set<NodeDescriptor> lockupNodes(Package pk) {
    Set<NodeDescriptor> result = new HashSet<>();
    try {
      ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
      provider.addIncludeFilter(new AssignableTypeFilter(Node.class));
      Set<BeanDefinition> candidates = Collections.emptySet();
      candidates = provider.findCandidateComponents(pk == null ? "" : pk.getName());
      for (BeanDefinition def : candidates) {
        if (def.isAbstract())
          continue;

        try {
          Class<Node> c = (Class<Node>) Class.forName(def.getBeanClassName());

          // Has default constructor?
          try {
            c.getConstructor();
          } catch (NoSuchMethodException e) {
            continue;
          }

          result.add(new NodeDescriptor(c));
        } catch (Throwable th) {
          System.err.println("Cannot instantiate workflow configuration " + def.getBeanClassName());
          th.printStackTrace();
        }
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return result;
  }

}

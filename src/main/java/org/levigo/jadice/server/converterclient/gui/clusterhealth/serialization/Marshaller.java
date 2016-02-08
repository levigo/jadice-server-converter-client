package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1.V1Marshaller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Marshaller {
  
  public static final class ClusterHealthDTO implements Observable {
    
    public final ObservableList<String> instances;
    
    public final ObservableList<Rule<?>> rules;
    
    public final BooleanProperty autoUpdateEnabled;
    
    public final IntegerProperty autoUpdateInterval;

    private final HashSet<InvalidationListener> listeners = new HashSet<>();
    
    public ClusterHealthDTO() {
      this(FXCollections.observableArrayList(), FXCollections.observableArrayList(), new SimpleBooleanProperty(false), new SimpleIntegerProperty(1));
    }
    
    public ClusterHealthDTO(ObservableList<String> instances, ObservableList<Rule<?>> rules, BooleanProperty autoUpdateEnabled, IntegerProperty autoUpdateInterval) {
      this.instances = Objects.requireNonNull(instances);
      this.rules = Objects.requireNonNull(rules);
      this.autoUpdateEnabled = Objects.requireNonNull(autoUpdateEnabled);
      this.autoUpdateInterval = Objects.requireNonNull(autoUpdateInterval);
      
      // Great use case for java 8: this DTO implements InvalidationListener without implementing it!
      this.instances.addListener(this::propertyInvalidated);
      this.rules.addListener(this::propertyInvalidated);
      this.autoUpdateEnabled.addListener(this::propertyInvalidated);
      this.autoUpdateInterval.addListener(this::propertyInvalidated);
    }

    @Override
    public void addListener(InvalidationListener listener) {
      listeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
      listeners.remove(Objects.requireNonNull(listener));
    }

    private void propertyInvalidated(Observable observable) {
      listeners.forEach(it -> it.invalidated(this));
    }
  }
  
  public static Marshaller getDefault() {
    return new V1Marshaller();
  }
  
  public static Marshaller get(String version) throws MarshallingException {
    Objects.requireNonNull(version, "version");
    switch (version) {
      case "1.0" : 
        return new V1Marshaller();
      
      default :
        throw new MarshallingException("no support for serialization format version " + version);
    }
  }
  
  public static String lookupVersion(String serialized) throws MarshallingException {
    try {
      final JsonParser parser = new JsonFactory().createParser(serialized);
      while (parser.nextToken() != null) {
        if (parser.getCurrentToken().isScalarValue() && parser.getCurrentName() == "version") {
          return parser.getValueAsString();
        }
      }
    } catch (IOException e) {
      throw new MarshallingException("Could not read serialization version", e);
    }
    throw new MarshallingException("No serialization version found");
  }
  
  public abstract String marshall(ClusterHealthDTO dto) throws MarshallingException;
  
  public abstract String marshallPrettyPrint(ClusterHealthDTO dto) throws MarshallingException;

  public abstract ClusterHealthDTO unmarshall(String s) throws MarshallingException;

}

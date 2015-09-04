package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization;

import java.io.IOException;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1.V1Marshaller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.levigo.util.base.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Marshaller {
  
  // TODO: Make the whole DTO Observable so that we don't need to register the Preferences to all of its content elements!
  public static final class ClusterHealthDTO {
    
    public ObservableList<String> instances;
    
    public ObservableList<Rule<?>> rules;
    
    public BooleanProperty autoUpdateEnabled;
    
    public IntegerProperty autoUpdateIntervall;

    public ClusterHealthDTO() {
      this(FXCollections.observableArrayList(), FXCollections.observableArrayList(), new SimpleBooleanProperty(false), new SimpleIntegerProperty(1));
    }
    
    public ClusterHealthDTO(ObservableList<String> instances, ObservableList<Rule<?>> rules, BooleanProperty autoUpdateEnabled, IntegerProperty updateIntervall) {
      this.instances = instances;
      this.rules = rules;
      this.autoUpdateEnabled = autoUpdateEnabled;
      this.autoUpdateIntervall = updateIntervall;
    }
    
  }
  
  public static Marshaller getDefault() {
    return new V1Marshaller();
  }
  
  public static Marshaller get(String version) throws MarshallingException {
    Objects.assertNotNull("version", version);
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

package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.EvaluationResult;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;

public class ClusterInstance {

  private final String jmxUrl;

  private final StringProperty serverNameProperty;

  private final ObjectProperty<HealthStatus> healthProperty;

  private final ListProperty<String> messagesProperty;
  
  private final ObservableList<Rule<?>> rules; 

  public ClusterInstance(String jmxUrl, ObservableList<Rule<?>> rules) {
    this.jmxUrl = jmxUrl;
    this.rules = rules;

    serverNameProperty = new SimpleStringProperty(jmxUrl);
    healthProperty = new SimpleObjectProperty<>(HealthStatus.UNKNOW);
    messagesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
  }

  public StringProperty serverNameProperty() {
    return serverNameProperty;
  }

  public ObjectProperty<HealthStatus> healthProperty() {
    return healthProperty;
  }

  public ListProperty<String> messagesProperty() {
    return messagesProperty;
  }

  public void update() {
    try {
      try (final JMXConnector connector = JmxHelper.createConnector(this.jmxUrl)) {
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();
        final List<EvaluationResult<?>> status = checkRules(mbsc);
        messagesProperty.setAll(filterMessages(status));

        if (status.stream().anyMatch(s -> s.status == HealthStatus.FAILURE)) {
          healthProperty.set(HealthStatus.FAILURE);
          return;
        }
        
        if (status.stream().anyMatch(s -> s.status == HealthStatus.ATTENTION)) {
          healthProperty.set(HealthStatus.ATTENTION);
          return;
        }
      }
      
      healthProperty.set(HealthStatus.GOOD);

    } catch (JMException | IOException e) {
      healthProperty.set(HealthStatus.FAILURE);
      messagesProperty.setAll(e.getMessage());
    }
  }

  private static List<String> filterMessages(final List<EvaluationResult<?>> results) {
    return results.stream().map(f -> f.message).filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList());
  }
  
  private List<EvaluationResult<?>> checkRules(MBeanServerConnection mbsc) {
    return rules.stream().map(rules -> rules.evaluate(mbsc)).collect(Collectors.toList());
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;

public class ClusterInstance {

  private final String jmxUrl;

  private final StringProperty serverNameProperty;

  private final ObjectProperty<HealthStatus> healthProperty;

  private final ListProperty<String> messagesProperty;
  
  private final ObservableList<Rule> rules; 

  public ClusterInstance(String jmxUrl, ObservableList<Rule> rules) {
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
        final List<String> failures = checkRules(mbsc, HealthStatus.FAILURE);
        if (!failures.isEmpty()) {
          healthProperty.set(HealthStatus.FAILURE);
          messagesProperty.setAll(failures);
          return;
        }
        final List<String> warnings = checkRules(mbsc, HealthStatus.ATTENTION);
        if (!warnings.isEmpty()) {
          healthProperty.set(HealthStatus.ATTENTION);
          messagesProperty.setAll(warnings);
          return;
        }
      }
      healthProperty.set(HealthStatus.GOOD);
    } catch (JMException | IOException e) {
      healthProperty.set(HealthStatus.FAILURE);
      messagesProperty.setAll(e.getMessage());
    }
  }

  private List<String> checkRules(MBeanServerConnection mbsc, HealthStatus status) {
    final List<String> failedMessages = rules.stream().filter(r -> r.getStatus() == status).map(r -> {
      try {
        return r.check(mbsc);
      } catch (JMException e) {
        return Optional.of(e.getMessage());
      }
    }).filter(optional -> optional.isPresent()).map(optional -> optional.get()).collect(Collectors.toList());
    return failedMessages;
  }
}

package org.levigo.jadice.server.converterclient.gui.clusterhealth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.levigo.jadice.server.converterclient.Preferences;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.EvaluationResult;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class ClusterInstance {

  private final String jmxUrl;

  private final StringProperty serverNameProperty;

  private final ObjectProperty<HealthStatus> healthProperty;

  private final ListProperty<String> messagesProperty;
  
  public ClusterInstance(String jmxUrl) {
    this.jmxUrl = jmxUrl;

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
    final List<Rule<?>> rules = Preferences.clusterHealthProperty().getValue().rules;
    if (rules.isEmpty()) {
      healthProperty.set(HealthStatus.UNKNOW);
      return;
    }

    try (final JMXConnector connector = JmxHelper.createConnector(this.jmxUrl)) {
      MBeanServerConnection mbsc = connector.getMBeanServerConnection();
      final List<EvaluationResult<?>> status = checkRules(mbsc, rules);

      final Optional<HealthStatus> relevantStatus = status.stream().map(r -> r.status).sorted(HealthStatus::severeFirst).findFirst();
      healthProperty.set(relevantStatus.orElse(HealthStatus.UNKNOW));
      messagesProperty.setAll(filterMessages(status));

    } catch (JMException | IOException e) {
      healthProperty.set(HealthStatus.FAILURE);
      messagesProperty.setAll(e.getMessage());
    }
  }

  private static List<String> filterMessages(final List<EvaluationResult<?>> results) {
    return results.stream().map(f -> f.message).filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList());
  }
  
  private List<EvaluationResult<?>> checkRules(MBeanServerConnection mbsc, List<Rule<?>> rules) {
    return rules.stream().map(rule -> rule.evaluate(mbsc)).collect(Collectors.toList());
  }
}

package org.levigo.jadice.server.converterclient;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;
import org.levigo.jadice.server.converterclient.util.Objects;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.Node;
import com.levigo.jadice.server.client.jms.JMSJobFactory;
import com.levigo.jadice.server.nodes.StreamInputNode;
import com.levigo.jadice.server.nodes.StreamOutputNode;
import com.levigo.jadice.server.util.NodeTraversal;
import com.levigo.jadice.server.util.NodeVisitor;

public class JobCardFactory {

  private static Logger LOGGER = Logger.getLogger(JobCardFactory.class);

  private static JobCardFactory instance = new JobCardFactory();

  public static JobCardFactory getInstance() {
    return instance;
  }

  private JobCardFactory() {
  }

  private Map<String, WorkflowConfiguration> configurations = null;

  private Set<JobCardListener> listeners = new HashSet<>();
  
  public ObservableList<WorkflowConfiguration> getConfigurations() {
    TreeSet<WorkflowConfiguration> result = new TreeSet<>(new Comparator<WorkflowConfiguration>() {
      public int compare(WorkflowConfiguration conf1, WorkflowConfiguration conf2) {
        return conf1.getDescription().compareToIgnoreCase(conf2.getDescription());
      }
    });
    result.addAll(lookupConfigurations().values());
    return FXCollections.observableArrayList(result);
  }

  @SuppressWarnings("unchecked")
  private Map<String, WorkflowConfiguration> lookupConfigurations() {
    if (configurations == null) {
      // Finde alle Configuration-Klassen mit Spring-Mitteln
      configurations = new HashMap<>();

      ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
      provider.addIncludeFilter(new AssignableTypeFilter(WorkflowConfiguration.class));
      Set<BeanDefinition> candidates = Collections.emptySet();
      try {
        candidates = provider.findCandidateComponents(getClass().getPackage().getName() + ".configurations");
      } catch (Exception e) {
        LOGGER.error("Cannot lookup workflow configurations", e);
      }
      for (BeanDefinition def : candidates) {
        if (def.isAbstract())
          continue;

        try {
          Class<WorkflowConfiguration> c = (Class<WorkflowConfiguration>) Class.forName(def.getBeanClassName());

          // Has default constructor?
          try {
            c.getConstructor();
          } catch (NoSuchMethodException e) {
            continue;
          }

          WorkflowConfiguration instance = c.newInstance();
          configurations.put(instance.getID(), instance);
        } catch (Throwable th) {
          LOGGER.error("Cannot instantiate workflow configuration " + def.getBeanClassName(), th);
        }
      }
    }
    return configurations;
  }

  public WorkflowConfiguration getConfiguration(String id) {
    return lookupConfigurations().get(id);

  }

  public JobCard createAndSubmitJobCard(File file, String serverLocation, WorkflowConfiguration config)
      throws Exception {
    return createAndSubmitJobCard(Collections.singletonList(file), serverLocation, config);
  }


  public JobCard createAndSubmitJobCard(List<File> files, String serverLocation, WorkflowConfiguration config)
      throws Exception {

    JMSJobFactory jobFactory = getCachedJobFactory(serverLocation);
    // Configure Workflow
    Job job = config.configureWorkflow(jobFactory);
    job.setType(config.getID());

    // Sanity check
    Collection<StreamInputNode> inputNodes = findNodes(job, StreamInputNode.class);

    if (inputNodes.size() != 1) {
      throw new IllegalArgumentException("Configuration must have exactly 1 StreamInputNode");
    }

    StreamInputNode inputNode = inputNodes.iterator().next();

    JobCard jobCard = new JobCard(job, inputNode, findNodes(job, StreamOutputNode.class), files, config);

    for (JobCardListener l : listeners)
      l.jobCardCreated(jobCard);

    JobCardScheduler.getInstance().submit(jobCard);

    return jobCard;
  }


  private JMSJobFactory jobFactory;

  private ActiveMQConnectionFactory amqConnectionFactory;
  
  private String jmsRequestQueueName;

  private JMSJobFactory getCachedJobFactory(String serverLocation) throws JMSException {
    final String jmsUsername = Preferences.jmsUsernameProperty().getValue();
    final String jmsPassword = Preferences.jmsPasswordProperty().getValue();
    final String jmsRequestQueueName = Preferences.jmsRequestQueueNameProperty().getValue();
    final int jmsJobPriority = Preferences.jmsJobPriority().getValue();

    boolean mustRecreate = !Preferences.cacheJmsJobFactoryProperty().getValue();
    if (jobFactory == null || amqConnectionFactory == null || jmsRequestQueueName == null) {
      mustRecreate = true;
    }
    
    if (mustRecreate //
        || !Objects.equals(amqConnectionFactory.getBrokerURL(), serverLocation)//
        || !Objects.equals(amqConnectionFactory.getUserName(), jmsUsername)//
        || !Objects.equals(amqConnectionFactory.getPassword(), jmsPassword)//
        || !Objects.equals(this.jmsRequestQueueName, jmsRequestQueueName)
        || jobFactory.getDefaultPriority() != jmsJobPriority) {
      mustRecreate = true;
    }
    
    if (mustRecreate) {
      this.jmsRequestQueueName = jmsRequestQueueName;
      amqConnectionFactory = new ActiveMQConnectionFactory(serverLocation);
      amqConnectionFactory.setUserName(jmsUsername);
      amqConnectionFactory.setPassword(jmsPassword);
      jobFactory = new JMSJobFactory(amqConnectionFactory, jmsRequestQueueName);
      jobFactory.setDefaultPriority(jmsJobPriority);
      
    }
    return jobFactory;
  }

  public JobCard cloneAndSubmitJob(JobCard oldJob, String serverLocation) throws Exception {
    return createAndSubmitJobCard(oldJob.files, serverLocation, oldJob.config);

  }

  public void addListener(JobCardListener listener) {
    listeners.add(listener);
  }

  protected static <N extends Node> Collection<N> findNodes(Job job, final Class<N> type) {
    final Collection<N> results = new HashSet<N>();
    NodeTraversal.traverse(job.getNode(), new NodeVisitor<RuntimeException>() {
      public void visit(Node node) {
        if (type.isAssignableFrom(node.getClass()))
          results.add(type.cast(node));
      }
    }, false);
    return results;
  }
}

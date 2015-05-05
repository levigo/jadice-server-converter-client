package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentAverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentEfficiencyRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ServerRunningRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.MarshallingDTO;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1.V1Marshaller;

public class TestMarshaller {
  
  private static final String V1 = "1.0";
  
  private static final String SERIALIZED_BY_V1 = "{\"version\":1.0,\"rules\":[{\"limit\":15.0,\"implementation\":\"org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule\"}],\"instances\":[\"localhost:61619\",\"jadice-server.example.com:61619\"]}";

  private static List<Rule<?>> RULES;
  
  private static List<String> INSTANCES;
  
  @BeforeClass
  public static void createRules() {
    RULES = new ArrayList<>();
    RULES.add(new ServerRunningRule());
    RULES.add(new AverageExecutionTimeRule(10L));
    RULES.add(new RecentAverageExecutionTimeRule(20L));
    RULES.add(new RecentEfficiencyRule(30f));
    RULES.add(new RecentFailureRateRule(40f));
    RULES.add(new TotalFailureRateRule(50f));
  }
  
  @BeforeClass
  public static void createInstances() {
    INSTANCES = new ArrayList<>();
    INSTANCES.add("localhost:61619");
    INSTANCES.add("jadice-server.example.com:61619");
  }
  
  @Test
  public void testLookupVersion() throws Exception {
    assertEquals("Version mismatch", "42.0", Marshaller.lookupVersion("{\"version\": \"42.0\"}"));
  }
  
  @Test
  public void testMarshallerV1Available() throws Exception {
    assertNotNull("No marshaller given", Marshaller.get(V1));
    assertEquals("Wrong marshaller given", V1Marshaller.class, Marshaller.get(V1).getClass());
  }
  
  @Test(expected = MarshallingException.class)
  public void testNoV99MarshallerAvailable() throws Exception {
    Marshaller.get("99");
  }
  
  @Test
  public void testV1Marshalling() throws Exception {
    final String m = Marshaller.get(V1).marshall(INSTANCES, RULES);
    assertEquals("Wrong version marshalled", "1.0", Marshaller.lookupVersion(m));
    final MarshallingDTO unmarshalled = Marshaller.get(V1).unmarshall(m);

    assertArrayEquals("Wrong instances", INSTANCES.toArray(), unmarshalled.instances.toArray());
    assertArrayEquals("Wrong rules", RULES.toArray(), unmarshalled.rules.toArray());
  }
  
  @Test
  public void testV1Unmarshalling() throws Exception {
    String version = Marshaller.lookupVersion(SERIALIZED_BY_V1);
    assertEquals("Wrong version detected", V1, version);

    final MarshallingDTO unmarshalled = Marshaller.get(V1).unmarshall(SERIALIZED_BY_V1);
    
    assertArrayEquals("Wrong instances", //
        new String[] {"localhost:61619","jadice-server.example.com:61619"}, //
        unmarshalled.instances.toArray());
    
    assertArrayEquals("Wrong rules", //
        new Rule<?>[] { new TotalFailureRateRule(15f)}, //
        unmarshalled.rules.toArray());
  }
}

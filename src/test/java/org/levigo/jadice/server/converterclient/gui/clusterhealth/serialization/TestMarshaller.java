package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.levigo.jadice.server.converterclient.Preferences.Defaults;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.AverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentAverageExecutionTimeRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentEfficiencyRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.RecentFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ServerRunningRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.TotalFailureRateRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.ClusterHealthDTO;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1.V1Marshaller;

import com.levigo.jadice.server.util.Util;

public class TestMarshaller {

  private static final String V1 = "1.0";

  private static final String SERIALIZED_BY_V1_RESOURCE = "/clusterhealth/serialization/V1.json";

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
  public void testEmptySerialization() throws Exception {
    final ClusterHealthDTO dto1 = new ClusterHealthDTO();
    dto1.instances = Collections.emptyList();
    dto1.rules = Collections.emptyList();
    Marshaller m = Marshaller.getDefault();
    final ClusterHealthDTO dto2 = m.unmarshall(m.marshall(dto1));

    assertTrue("instances shall be empty", dto2.instances.isEmpty());
    assertTrue("rules shall be empty", dto2.rules.isEmpty());
  }

  @Test
  public void testUnmarshallDefault() throws Exception {
    final String v = Marshaller.lookupVersion(Defaults.CLUSTER_HEALTH);
    assertFalse("No version found", v.isEmpty());
    
    final ClusterHealthDTO dto = Marshaller.get(v).unmarshall(Defaults.CLUSTER_HEALTH);
    assertNotNull("No object unmarshalled", dto);
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
    final ClusterHealthDTO unmarshalled = Marshaller.get(V1).unmarshall(m);

    assertArrayEquals("Wrong instances", INSTANCES.toArray(), unmarshalled.instances.toArray());
    assertArrayEquals("Wrong rules", RULES.toArray(), unmarshalled.rules.toArray());
  }

  @Test
  public void testV1Unmarshalling() throws Exception {
    final String json = loadJSON(SERIALIZED_BY_V1_RESOURCE);
    
    String version = Marshaller.lookupVersion(json);
    assertEquals("Wrong version detected", V1, version);

    final ClusterHealthDTO unmarshalled = Marshaller.get(V1).unmarshall(json);

    assertArrayEquals("Wrong instances", INSTANCES.toArray(), unmarshalled.instances.toArray());
    assertArrayEquals("Wrong rules", RULES.toArray(), unmarshalled.rules.toArray());
  }

  private static String loadJSON(String resourceName) throws IOException {
    try (final InputStream is = TestMarshaller.class.getResourceAsStream(resourceName)) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Util.copyAndClose(is, baos);
      return new String(baos.toByteArray(), "UTF-8");
    }
  }
}

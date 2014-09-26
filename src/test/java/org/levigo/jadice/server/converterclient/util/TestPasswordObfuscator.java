package org.levigo.jadice.server.converterclient.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class TestPasswordObfuscator {

  private static final String MY_PASSWORD = "Test-123456-äöüß";

  @Test
  public void testObfucation() throws Exception {
    final String obfuscated = PasswordObfuscator.obfuscate(MY_PASSWORD);
    assertNotNull("No obfuscation result", obfuscated);
    final String deobfuscated = PasswordObfuscator.deobfuscate(obfuscated);
    assertNotNull("No deobfuscation result", deobfuscated);

    assertEquals("De-Obfuscation failed", MY_PASSWORD, deobfuscated);
  }

  /**
   * Check if deobfuscator is backword compatible and does not touch passwords stored in plain text
   * 
   * @throws Exception
   */
  @Test
  public void testBackwardCompability() throws Exception {
    assertEquals("De-Obfuscation failed", MY_PASSWORD, PasswordObfuscator.deobfuscate(MY_PASSWORD));
  }

}

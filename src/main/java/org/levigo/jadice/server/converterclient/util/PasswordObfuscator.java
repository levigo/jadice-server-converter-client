package org.levigo.jadice.server.converterclient.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Obfuscate a password in order to store it in the windows registry.
 * 
 * Inspired by http://stackoverflow.com/a/1133815
 */
public class PasswordObfuscator {

  private static final String UTF8 = "UTF-8";

  private static final String ALGORITHM = "PBEWithMD5AndDES";

  private static final char[] PASSWORD = "jadice-server-converter-client".toCharArray();

  private static final String PREFIX = "crypt:";

  private static final byte[] SALT = {
      (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
  };
  
  private PasswordObfuscator() {
    // hidden constr.
  }

  public static String obfuscate(String property) throws GeneralSecurityException, UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
    Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return PREFIX + base64Encode(pbeCipher.doFinal(property.getBytes(UTF8)));
  }

  private static String base64Encode(byte[] bytes) throws UnsupportedEncodingException {
    return new String(Base64.getMimeEncoder().encode(bytes), UTF8);
  }

  public static String deobfuscate(String property) throws GeneralSecurityException, IOException {
    if (property == null || !property.startsWith(PREFIX)) {
      return property;
    }
    property = property.substring(PREFIX.length());
    
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
    Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), UTF8);
  }

  private static byte[] base64Decode(String property) throws IOException {
    return Base64.getMimeDecoder().decode(property.getBytes(UTF8));
  }

}
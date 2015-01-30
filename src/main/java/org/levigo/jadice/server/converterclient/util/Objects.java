package org.levigo.jadice.server.converterclient.util;

public class Objects {
  
  private Objects() {
    // hidden constr.
  }
  
  @Deprecated
  public static boolean equals(Object a, Object b) {
    if (a == b) {
      return true;
    }
    
    if (a == null ^ b == null) {
      return false;
    }
    
    return a.equals(b);
  }

}

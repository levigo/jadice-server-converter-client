package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ImmutableBooleanRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.NumericRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.MarshallingDTO;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.MarshallingException;

public class ClusterHealthMapper {
  
  public ClusterHealth map(MarshallingDTO dto) throws MarshallingException {
    final ClusterHealth result = new ClusterHealth();
    
    result.instances = dto.instances;
    
    for (org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.Rule<?> rule : dto.rules) {
      if (rule instanceof NumericRule<?>) {
        Rule<Number> r = new Rule<>();
        r.limit = ((NumericRule<?>) rule).getLimit();
        r.implementation = rule.getClass().getName();
        result.rules.add(r);
      } else if (rule instanceof ImmutableBooleanRule) {
        Rule<Boolean> r = new Rule<>();
        r.implementation = rule.getClass().getName();
        result.rules.add(r);
      } else {
        throw new MarshallingException("No support for rule of type " + rule.getClass());
      }
    }
    return result;
  }
  
  public MarshallingDTO unmap(ClusterHealth ch) throws MarshallingException {
    MarshallingDTO result = new MarshallingDTO();
    result.instances = ch.instances;
    result.rules = new ArrayList<>(ch.rules.size());
    
    for (Rule<?> rule : ch.rules) {
      try {
        final Class<?> clazz = Class.forName(rule.implementation);
        Constructor<?> constr = null;
        if (NumericRule.class.isAssignableFrom(clazz)) {
          for (Constructor<?> c : clazz.getConstructors()) {
            if (c.getParameterCount() != 1) {
              continue;
            }
            constr = c;
            break;
          }
          if (constr == null) {
            throw new MarshallingException("No matching constructor found for type " + rule.implementation);
          }
          System.out.println();
          final NumericRule<?> r = (NumericRule<?>) constr.newInstance(castValue(constr.getParameterTypes()[0], rule.limit));
          result.rules.add(r);
        } else if (ImmutableBooleanRule.class.isAssignableFrom(clazz)) {
          final ImmutableBooleanRule r = (ImmutableBooleanRule) clazz.newInstance();
          result.rules.add(r);
        } else {
          throw new MarshallingException("No support for rule of type " + rule.implementation);
        }
      } catch (ReflectiveOperationException | SecurityException e) {
        throw new MarshallingException("Could not unmarshall rule of type " + rule.implementation, e);
      }
    }

    return result;
  }
  
  private static Number castValue(Class<?> target, Object o) throws MarshallingException {
    if (!Number.class.isAssignableFrom(o.getClass())) {
      throw new MarshallingException(o + " ("+ o.getClass() +") is not a number");
    }
    Number nmbr = (Number) o;
    if (target == Float.TYPE) {
      return nmbr.floatValue();
    } else if (target == Long.TYPE) {
      return nmbr.longValue();
    } else if (target == Double.TYPE) {
      return nmbr.doubleValue();
    } else {
      throw new MarshallingException("No numeric conversion for type " + target.getSimpleName() + " available");
    }
  }

}

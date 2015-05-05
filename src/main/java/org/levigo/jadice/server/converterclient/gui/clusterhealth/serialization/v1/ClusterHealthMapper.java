package org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.v1;

import java.lang.reflect.Constructor;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.ImmutableBooleanRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.rule.NumericRule;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.Marshaller.ClusterHealthDTO;
import org.levigo.jadice.server.converterclient.gui.clusterhealth.serialization.MarshallingException;

public class ClusterHealthMapper {
  
  public ClusterHealth map(ClusterHealthDTO dto) throws MarshallingException {
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
  
  public ClusterHealthDTO unmap(ClusterHealth ch) throws MarshallingException {
    ClusterHealthDTO result = new ClusterHealthDTO();
    result.instances.addAll(ch.instances);
    
    for (Rule<?> rule : ch.rules) {
      try {
        final Class<?> clazz = Class.forName(rule.implementation);
        if (NumericRule.class.isAssignableFrom(clazz)) {
          final NumericRule<?> r = unmarshallNumericRule(rule, clazz);
          result.rules.add(r);
        } else if (ImmutableBooleanRule.class.isAssignableFrom(clazz)) {
          final ImmutableBooleanRule r = unmarshallImmutableBooleanRule(clazz);
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

  private ImmutableBooleanRule unmarshallImmutableBooleanRule(final Class<?> clazz) throws ReflectiveOperationException {
    return (ImmutableBooleanRule) clazz.newInstance();
  }

  private NumericRule<?> unmarshallNumericRule(Rule<?> rule, final Class<?> clazz) throws ReflectiveOperationException, MarshallingException {
    Constructor<?> constr = null;
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
    final NumericRule<?> r = (NumericRule<?>) constr.newInstance(castValue(constr.getParameterTypes()[0], rule.limit));
    return r;
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

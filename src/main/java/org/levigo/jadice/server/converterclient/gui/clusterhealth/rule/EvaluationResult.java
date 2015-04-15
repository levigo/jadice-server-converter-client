package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Optional;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

  public  class EvaluationResult<T> {
    
    public final HealthStatus status;
    
    public final Optional<String> message;
    
    public final T currentValue;
    
    public final Optional<Throwable> error;
    
    public EvaluationResult(HealthStatus status, T currentValue) {
      this(status, currentValue, Optional.empty(), Optional.empty());
    }
    
    public EvaluationResult(HealthStatus status, T currentValue, String message) {
      this(status, currentValue, Optional.of(message), Optional.empty());
      
    }
    
    public EvaluationResult(HealthStatus status, T currentValue, String message, Throwable error) {
      this(status, currentValue, Optional.of(message), Optional.of(error));
    }
    
    public EvaluationResult(HealthStatus status, T currentValue, Throwable error) {
      this(status, currentValue, Optional.empty(), Optional.of(error));
    }
    
    private EvaluationResult(HealthStatus status, T currentValue, Optional<String> message, Optional<Throwable> error) {
      this.status = status;
      this.currentValue = currentValue;
      this.message = message;
      this.error = error;
      
    }

    @Deprecated
    public EvaluationResult(HealthStatus status, Optional<String> message, T currentValue) {
      this.status = status;
      this.message = message;
      this.currentValue = currentValue;
      this.error = Optional.empty();
    }
  }
  

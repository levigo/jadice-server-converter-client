package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Objects;
import java.util.Optional;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

public class EvaluationResult<T> {

  public final HealthStatus status;

  public final Optional<String> message;

  public final Optional<T> currentValue;

  public final Optional<Throwable> error;
  
  public EvaluationResult(HealthStatus status) {
    this(status, Optional.empty(), Optional.empty(), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, T currentValue) {
    this(status, Optional.of(currentValue), Optional.empty(), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, T currentValue, String message) {
    this(status, Optional.of(currentValue), Optional.of(message), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, String message) {
    this(status, Optional.empty(), Optional.of(message), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, Throwable error) {
    this(status, Optional.empty(), Optional.empty(), Optional.of(error));
  }

  private EvaluationResult(HealthStatus status, Optional<T> currentValue, Optional<String> message, Optional<Throwable> error) {
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.currentValue = Objects.requireNonNull(currentValue, "currentValue must not be null");
    this.message = Objects.requireNonNull(message, "message must not be null");
    this.error = Objects.requireNonNull(error, "error must not be null");
  }
}

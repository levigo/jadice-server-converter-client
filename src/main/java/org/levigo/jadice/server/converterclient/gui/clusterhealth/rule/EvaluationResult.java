package org.levigo.jadice.server.converterclient.gui.clusterhealth.rule;

import java.util.Optional;

import org.levigo.jadice.server.converterclient.gui.clusterhealth.HealthStatus;

public class EvaluationResult<T> {

  public final HealthStatus status;

  public final Optional<String> message;

  public final Optional<T> currentValue;

  public final Optional<Throwable> error;

  public EvaluationResult(HealthStatus status, T currentValue) {
    this(status, Optional.of(currentValue), Optional.empty(), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, T currentValue, String message) {
    this(status, Optional.of(currentValue), Optional.of(message), Optional.empty());
  }

  public EvaluationResult(HealthStatus status, Throwable error) {
    this(status, Optional.empty(), Optional.empty(), Optional.of(error));
  }

  private EvaluationResult(HealthStatus status, Optional<T> currentValue, Optional<String> message, Optional<Throwable> error) {
    this.status = status;
    this.currentValue = currentValue;
    this.message = message;
    this.error = error;
  }
}

package io.magentys.cherry.reactive.exceptions;

@SuppressWarnings("unused")
public class ReactiveAgentHasFailed extends RuntimeException
{
  public ReactiveAgentHasFailed(String message)
  {
    super(message);
  }
}

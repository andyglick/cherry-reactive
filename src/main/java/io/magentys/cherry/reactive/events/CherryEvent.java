package io.magentys.cherry.reactive.events;

@SuppressWarnings("unused")
public interface CherryEvent<BODY, METADATA>
{
  BODY body();

  String name();

  METADATA metadata();
}

package io.magentys.cherry.reactive.events;

public interface CherryEvent<BODY,METADATA> {

    BODY body();

    String name();

    METADATA metadata();

}

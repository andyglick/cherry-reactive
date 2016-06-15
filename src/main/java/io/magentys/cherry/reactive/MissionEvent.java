package io.magentys.cherry.reactive;

public interface MissionEvent<BODY,METADATA> {

    BODY body();

    String name();

    METADATA metadata();

}

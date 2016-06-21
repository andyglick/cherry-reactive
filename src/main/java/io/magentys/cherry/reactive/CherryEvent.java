package io.magentys.cherry.reactive;

public interface CherryEvent<BODY,METADATA> {

    BODY body();

    String name();

    METADATA metadata();

}

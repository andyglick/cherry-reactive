package io.magentys.cherry.reactive;

import java.util.Set;

public interface MissionEventRegistry {

    void addEvents(CherryEvent event);

    Set<CherryEvent> events();

}

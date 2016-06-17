package io.magentys.cherry.reactive;

import java.util.Set;

public interface MissionEventRegistry {

    void addEvents(MissionEvent event);

    Set<MissionEvent> events();

}

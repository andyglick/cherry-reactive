package io.magentys.cherry.reactive.events;

import io.magentys.cherry.reactive.ReactiveAgent;
import io.magentys.cherry.reactive.ReactiveMission;

public class ReactiveMissionEvent implements CherryEvent<ReactiveAgent,ReactiveMission> {

    private final ReactiveMission mission;
    private final ReactiveAgent agent;

    public ReactiveMissionEvent(final ReactiveAgent agent, final ReactiveMission mission) {
        this.mission = mission;
        this.agent = agent;
    }

    public static ReactiveMissionEvent asEvent(final ReactiveAgent agent, final ReactiveMission mission){
        return new ReactiveMissionEvent(agent, mission);
    }

    @Override
    public ReactiveAgent body() {
        return agent;
    }

    @Override
    public String name() {
        return mission.name();
    }

    @Override
    public ReactiveMission metadata() {
        return mission;
    }
}

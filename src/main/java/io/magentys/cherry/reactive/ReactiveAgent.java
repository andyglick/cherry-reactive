package io.magentys.cherry.reactive;

import akka.actor.ActorSystem;
import io.magentys.Agent;
import io.magentys.Memory;
import io.magentys.java8.FunctionalAgent;

import java.util.Optional;


public class ReactiveAgent extends FunctionalAgent {

    static ActorSystem system;

    private Supervisor supervisor;
    private MissionStrategy defaultStrategy;


    public ReactiveAgent(Memory memory) {
        super(memory);
        system = ActorSystem.create(this.name());

    }

    public static ReactiveAgent create(Memory memory) {
        return new ReactiveAgent(memory);
    }

    public static ReactiveAgent createFrom(Agent agent) {
        return (ReactiveAgent) new ReactiveAgent(agent.getMemory()).setTools(agent.getTools());
    }

    public <RESULT> RESULT performs(ReactiveMission<RESULT> reactiveMission) {
        Optional<MissionStrategy> strategyToUse = getMissionStrategyFrom(reactiveMission);
        strategyToUse.ifPresent(missionSchedule -> missionSchedule.beforeMissions().stream().forEach(super::performs));
        RESULT result = super.performs(reactiveMission);
        strategyToUse.ifPresent(missionSchedule -> missionSchedule.afterMissions().stream().forEach(super::performs));
        return result;
    }

    private Optional<MissionStrategy> getMissionStrategyFrom(ReactiveMission reactiveMission){
        if(reactiveMission.hasStrategy()) return reactiveMission.strategy();
        if(iHaveDefaultStrategy()) return Optional.ofNullable(defaultStrategy);
        return Optional.empty();
    }

    private boolean iHaveDefaultStrategy() {
        return defaultStrategy != null;
    }


    public ReactiveAgent withDefaultStrategy(MissionStrategy missionStrategy) {
        this.defaultStrategy = missionStrategy;
        return this;
    }
}

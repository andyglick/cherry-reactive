package io.magentys.cherry.reactive.actors;

import akka.actor.UntypedActor;
import io.magentys.Agent;
import io.magentys.cherry.reactive.MissionStrategy;
import io.magentys.cherry.reactive.ReactiveMission;
import io.magentys.cherry.reactive.events.MultiMissionEvent;
import io.magentys.cherry.reactive.events.ReactiveMissionEvent;

import java.util.Optional;

public class CherryActor extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof MultiMissionEvent){
            MultiMissionEvent multiMissionEvent = (MultiMissionEvent) message;
            Agent agent = multiMissionEvent.body();
            multiMissionEvent.metadata().stream().forEach(agent::performs);
        }
        if(message instanceof ReactiveMissionEvent) {
            // Unwrap message
            ReactiveMissionEvent reactiveMissionEvent = (ReactiveMissionEvent) message;
            Agent agent = reactiveMissionEvent.body();
            ReactiveMission reactiveMission = reactiveMissionEvent.metadata();
            Optional<MissionStrategy> strategyToUse = reactiveMission.strategy();

            //before
            strategyToUse.ifPresent(missionSchedule -> missionSchedule.beforeMissions().stream().forEach(agent::performs));

            // act
            Object result = agent.performs(reactiveMission);

            // after
            strategyToUse.ifPresent(missionSchedule -> missionSchedule.afterMissions().stream().forEach(agent::performs));

            //Respond to sender
            getContext().sender().tell(result, getSelf());

        } else {
            unhandled(message);
        }
    }
}

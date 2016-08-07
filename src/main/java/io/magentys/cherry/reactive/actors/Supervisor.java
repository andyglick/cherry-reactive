package io.magentys.cherry.reactive.actors;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import io.magentys.Agent;
import io.magentys.Mission;
import io.magentys.cherry.reactive.MissionStrategy;
import io.magentys.cherry.reactive.ReactiveMission;
import io.magentys.cherry.reactive.events.ReactiveMissionEvent;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class Supervisor extends UntypedActor
{
  private SupervisorStrategy strategy =
    new OneForOneStrategy(10, Duration.create("1 minute"),
      throwable ->
      {
        if (throwable instanceof NullPointerException)
        {
          return SupervisorStrategy.restart();
        }
        else if (throwable instanceof IllegalArgumentException)
        {
          return SupervisorStrategy.stop();
        }
        else
        {
          return SupervisorStrategy.escalate();
        }
      }
    );

  @Override
  public SupervisorStrategy supervisorStrategy()
  {
    return strategy;
  }


  public void onReceive(Object o)
  {
    // Return instance
    if (o instanceof Props)
    {
      getSender().tell(getContext().actorOf((Props) o), getSelf());
    }

    // Set the supervision strategy
    if (o instanceof ReactiveMissionEvent)
    {
      ReactiveMissionEvent reactiveMissionEvent = (ReactiveMissionEvent) o;
      ReactiveMission mission = reactiveMissionEvent.metadata();
      Agent agent = reactiveMissionEvent.body();
      Optional<MissionStrategy> missionStrategyOpt = mission.strategy();
      missionStrategyOpt.ifPresent(missionStrategy ->
        strategy = new OneForOneStrategy(10, Duration.create("5 minutes"),
          throwable ->
          {
            List<Mission> missions = missionStrategy.exceptionToMissions().get(throwable.getClass());
            if (missions != null)
            {
              missions.forEach(agent::performs);
              return SupervisorStrategy.resume();
            }

            return SupervisorStrategy.resume();
          }
        ));

      getSender().tell("setStrategyCompleted", getSelf());
    }

    else
    {
      unhandled(o);
    }

  }


}

package io.magentys.cherry.reactive;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import io.magentys.Agent;
import io.magentys.Memory;
import io.magentys.cherry.reactive.exceptions.StrategyException;
import io.magentys.java8.FunctionalAgent;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


import static akka.pattern.Patterns.ask;
import static io.magentys.cherry.reactive.ReactiveMissionEvent.asEvent;

/**
 * Reactive Version of Cherry Agents
 */
public class ReactiveAgent extends FunctionalAgent {

    static ActorSystem system;
    private final ActorRef slave;

    private Boolean failed = false;

    private ActorRef master;
    private MissionStrategy defaultStrategy;
    private final FiniteDuration defaultTerminationTimeout = Duration.create(30, TimeUnit.SECONDS);
    private final FiniteDuration timeout = Duration.create(30, TimeUnit.SECONDS);

    private CherryActor cherryActor;

    /**
     * Default constructor
     * @param memory - implementation of memory
     */
    public ReactiveAgent(Memory memory) throws Exception {
        super(memory);
        system = ActorSystem.create(this.name());
        this.master = system.actorOf(Props.create(Supervisor.class), this.name());
        this.slave = (ActorRef) Await.result(ask(master, Props.create(CherryActor.class), 5000), timeout);
    }



    /**
     * terminates with default termination timeout set to 30 seconds
     */
    public void terminate() {  terminate(defaultTerminationTimeout);  }

    /**
     * terminates and awaits for termination to complete
     * @param timeout defines how long to wait for termination to complete
     */
    public void terminate(Duration timeout){
        system.terminate();
        system.awaitTermination(timeout);

    }

    /**
     * Static constructor
     * @param memory - implementation of memory
     * @return new ReactiveAgent instance
     */
    public static ReactiveAgent create(Memory memory) throws Exception {
        return new ReactiveAgent(memory);
    }

    public static ReactiveAgent createFrom(Agent agent) throws Exception {
        return (ReactiveAgent) new ReactiveAgent(agent.getMemory()).setTools(agent.getTools()).setNarrators(agent.getNarrators());
    }

    /**
     * Performs a reactive mission
     * @param reactiveMission - the mission to perform
     * @param <RESULT> - the type of the missions' result
     * @return the actual result of the mission
     */
    public <RESULT> RESULT performsReactively(ReactiveMission<RESULT> reactiveMission) {
        Optional<MissionStrategy> strategyToUse = decideStrategyToUse(reactiveMission);

        strategyToUse.ifPresent(strategy -> reactiveMission.withStrategy(strategy));
        RESULT result = null;
        try {
            String strategySet = (String) Await.result(ask(master, asEvent(this, reactiveMission), 5000), timeout);
            if(strategySet != "setStrategyCompleted") throw new StrategyException("not properly set");
            result = (RESULT) Await.result(ask(slave, asEvent(this, reactiveMission), 60000), timeout);
        } catch (Exception e) {
           failed = true;
        }
        return result;
    }

    /**
     * Performs a reactive mission
     * @param reactiveMission - the mission to perform
     * @param missionStrategy - the strategy to use
     * @param <RESULT> - the type of the mission's result
     * @return the actual result of the mission
     */
    public <RESULT> RESULT performsReactively(ReactiveMission<RESULT> reactiveMission, MissionStrategy missionStrategy){
        return performsReactively(reactiveMission.withStrategy(missionStrategy));
    }

    private Optional<MissionStrategy> decideStrategyToUse(ReactiveMission reactiveMission){
        if(reactiveMission.hasStrategy()) return reactiveMission.strategy();
        if(iHaveDefaultStrategy()) return Optional.ofNullable(defaultStrategy);
        return Optional.empty();
    }

    private boolean iHaveDefaultStrategy() {
        return defaultStrategy != null;
    }


    /**
     * Setter (With-er) of default Mission Strategy
     * @param missionStrategy to be used when no mission-specific strategy is defined
     * @return myself
     */
    public ReactiveAgent withDefaultStrategy(MissionStrategy missionStrategy) {
        this.defaultStrategy = missionStrategy;
        return this;
    }

    public Boolean hasFailed() {
        return failed;
    }
}

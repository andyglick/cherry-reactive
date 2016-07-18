package io.magentys.cherry.reactive;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.Memory;
import io.magentys.cherry.reactive.actors.CherryActor;
import io.magentys.cherry.reactive.actors.Supervisor;
import io.magentys.cherry.reactive.common.Either;
import io.magentys.cherry.reactive.exceptions.StrategyException;
import io.magentys.cherry.reactive.models.Failure;
import io.magentys.java8.FunctionalAgent;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static io.magentys.cherry.reactive.events.ReactiveMissionEvent.asEvent;

/**
 * Reactive Version of Cherry Agents
 */
public class ReactiveAgent extends FunctionalAgent {

    protected static ActorSystem system;
    private ActorRef slave;

    private Boolean failed = false;
    private Failure failure = Failure.empty();


    private ActorRef master;
    private MissionStrategy defaultStrategy;
    private final FiniteDuration defaultTerminationTimeout = Duration.create(30, TimeUnit.SECONDS);
    private final FiniteDuration timeout = Duration.create(1, TimeUnit.SECONDS);

    /**
     * Default constructor
     *
     * @param memory - implementation of memory
     */
    public ReactiveAgent(Memory memory) throws Exception {
        super(memory);
        initializeActorSystem();
    }

    private void initializeActorSystem() throws Exception {
        system = ActorSystem.create(this.name() + "-System");
        this.master = system.actorOf(Props.create(Supervisor.class), this.name());
        this.slave = (ActorRef) Await.result(ask(master, Props.create(CherryActor.class), 5000), timeout);
    }

    public ReactiveAgent(Memory memory, String name) throws Exception {
        super(memory);
        this.name = name;
        initializeActorSystem();
    }


    /**
     * terminates with default termination timeout set to 30 seconds
     */
    public void terminate() {
        terminate(defaultTerminationTimeout);
    }

    /**
     * terminates and awaits for termination to complete
     *
     * @param timeout defines how long to wait for termination to complete
     */
    public void terminate(Duration timeout) {
        system.terminate();
        system.awaitTermination(timeout);

    }

    /**
     * Static constructor
     *
     * @param memory - implementation of memory
     * @return new ReactiveAgent instance
     */
    public static ReactiveAgent create(Memory memory) throws Exception {
        return new ReactiveAgent(memory);
    }

    public static ReactiveAgent createFrom(Agent agent) throws Exception {
        return (ReactiveAgent) new ReactiveAgent(agent.getMemory()).setTools(agent.getTools()).setNarrators(agent.getNarrators());
    }

    public static ReactiveAgent create(CoreMemory coreMemory, String name) throws Exception {
        return new ReactiveAgent(coreMemory, name);
    }

    /**
     * Performs a reactive mission
     *
     * @param reactiveMission - the mission to perform
     * @param <RESULT>        - the type of the missions' result
     * @return the actual result of the mission
     */
    public <RESULT> Either<RESULT,Failure> performsReactively(ReactiveMission<RESULT> reactiveMission) {
        Optional<MissionStrategy> strategyToUse = decideStrategyToUse(reactiveMission);
        strategyToUse.ifPresent(strategy -> reactiveMission.withStrategy(strategy));
        Either<RESULT,Failure> result;
        try {
            String strategySet = (String) Await.result(ask(master, asEvent(this, reactiveMission), 1000), timeout);
            if (strategySet != "setStrategyCompleted") throw new StrategyException("not properly set");
            final FiniteDuration allowedDuration = reactiveMission.strategy().get().timeoutStrategy().first();
            Timeout timeoutFromStrategy = Timeout.durationToTimeout(allowedDuration);
//            Future<RESULT> t = (Future<RESULT>) ask(slave, asEvent(this, reactiveMission), timeoutFromStrategy);
            RESULT  r = (RESULT) Await.result(ask(slave, asEvent(this, reactiveMission), timeoutFromStrategy), allowedDuration);
            result = Either.left(r);
        } catch (Exception e) {
            failure = Failure.failure(e);
            result = Either.right(failure);
            failed = true;
        }
        return result;
    }

    /**
     * Performs a reactive mission
     *
     * @param reactiveMission - the mission to perform
     * @param missionStrategy - the strategy to use
     * @param <RESULT>        - the type of the mission's result
     * @return the actual result of the mission
     */
    public <RESULT> Either<RESULT,Failure> performsReactively(ReactiveMission<RESULT> reactiveMission, MissionStrategy missionStrategy) {
        return performsReactively(reactiveMission.withStrategy(missionStrategy));
    }

    private Optional<MissionStrategy> decideStrategyToUse(ReactiveMission reactiveMission) {
        if (reactiveMission.hasStrategy()) return reactiveMission.strategy();
        if (iHaveDefaultStrategy()) return Optional.ofNullable(defaultStrategy);
        return Optional.empty();
    }

    private boolean iHaveDefaultStrategy() {
        return defaultStrategy != null;
    }


    /**
     * Setter (With-er) of default Mission Strategy
     *
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

    public Failure getFailure() {
        return failure;
    }

    public <TYPE> TYPE getFailureAs(Class<TYPE> type){
        return failure.getContentAs(type);
    }

    public ReactiveAgent resetFailure(){
        failure = Failure.empty();
        failed = false;
        return this;
    }


}

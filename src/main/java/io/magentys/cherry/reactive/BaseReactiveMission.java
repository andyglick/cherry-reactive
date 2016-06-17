package io.magentys.cherry.reactive;

import io.magentys.Mission;
import scala.concurrent.duration.Duration;

import java.util.Optional;
import java.util.Set;


public abstract class BaseReactiveMission<RESULT> implements ReactiveMission<RESULT> {

    private MissionStrategy missionStrategy;


    private MissionStrategy myStrategy(){
        if(missionStrategy == null) {
            missionStrategy = new MissionStrategy();
        }
        return missionStrategy;
    }

    @Override
    public ReactiveMission<RESULT> on(Duration duration, Mission... missions) {
        myStrategy().on(duration, missions);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> on(Duration duration, Class<? extends Throwable> throwableClass) {
        myStrategy().on(duration, throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> timesToRetry(Integer times, Mission... missions) {
        myStrategy().timesToRetry(times, missions);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> timesToRetry(Integer times, Class<? extends Throwable> throwableClass) {
        myStrategy().timesToRetry(times, throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> on(MissionEvent event, Mission... missions) {
        myStrategy().on(event, missions);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> on(MissionEvent event, Class<? extends Throwable> throwableClass) {
        myStrategy().on(event, throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> on(Class<? extends Throwable> throwableEvent, Mission... missions) {
        myStrategy().on(throwableEvent, missions);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> on(Class<? extends Throwable> throwableEvent, Class<? extends Throwable> throwableClass) {
        myStrategy().on(throwableEvent, throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> onAnyException(Class<? extends Throwable> throwableClass) {
        myStrategy().onAnyException(throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> onAnyException(Mission... missions) {
        myStrategy().onAnyException(missions);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> onEvents(Set<MissionEvent> missionEvents, Mission... missions) {
        myStrategy().onEvents(missionEvents, missions);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> onEvents(Set<MissionEvent> missionEvents, Class<? extends Throwable> throwableClass) {
        myStrategy().onEvents(missionEvents, throwableClass);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> onExceptions(Set<Class<? extends Throwable>> events, Mission... missions) {
        myStrategy().onExceptions(events, missions);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> onExceptions(Set<Class<? extends Throwable>> events, Class<? extends Throwable> throwableClass) {
        myStrategy().onExceptions(events, throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> butFirst(Mission... missions) {
        myStrategy().butFirst(missions);
        return this;
    }


    @Override
    public ReactiveMission<RESULT> andFinally(Mission... missions) {
        myStrategy().andFinally(missions);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> andFinally(Class<? extends Throwable> throwableClass) {
        myStrategy().andFinally(throwableClass);
        return this;
    }

    @Override
    public ReactiveMission<RESULT> withSchedule(MissionStrategy missionStrategy) {
        this.missionStrategy = missionStrategy;
        return this;
    }

    @Override
    public Optional<MissionStrategy> strategy() {
        return Optional.ofNullable(missionStrategy);
    }

    @Override
    public Boolean hasStrategy() {
        return missionStrategy != null;
    }

    @Override
    public ReactiveMission<RESULT> setNarrateExecutionTime(final boolean shouldNarrateExecutionTime){
        myStrategy().setNarrateExecutionTime(shouldNarrateExecutionTime);
        return this;
    }

    @Override
    public Boolean shouldNarrateExecutionTime(){
       return myStrategy().shouldNarrateExecutionTime();
    }


}

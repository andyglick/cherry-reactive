package io.magentys.cherry.reactive;

import akka.japi.Pair;
import io.magentys.Mission;
import io.magentys.cherry.reactive.events.CherryEvent;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class MissionStrategy implements Eventful<MissionStrategy>{

    private List<Mission> beforeMissions = new ArrayList<>();
    private List<Mission> afterMissions = new ArrayList<>();
    private Pair<FiniteDuration,List<Mission>> durationToMissions = Pair.create(Duration.create(1, TimeUnit.SECONDS),new ArrayList<>());
    private Map<String, List<Mission>> eventToMissions = new ConcurrentHashMap<>();
    private Map<Class<? extends Throwable>, List<Mission>> exceptionToMissions  = new ConcurrentHashMap<>();
    private Integer timesToRetry = 0;
    private Boolean shouldNarrateExecutionTime = false;
    private boolean catchAllExceptions = false;
    private List<Mission> catchAllMissions = new ArrayList<>();

    MissionStrategy(){}

    public static MissionStrategy aStrategy() { return new MissionStrategy(); }

    public List<Mission> beforeMissions() {
        return beforeMissions;
    }


    public Pair<FiniteDuration,List<Mission>> timeoutStrategy() {
        return durationToMissions;
    }

    public Map<String, List<Mission>> eventToMissions() {
        return eventToMissions;
    }



    public Map<Class<? extends Throwable>, List<Mission>> exceptionToMissions() {
        return exceptionToMissions;
    }



    @Override
    public MissionStrategy timeout(FiniteDuration duration, Mission... missions) {
        durationToMissions = Pair.create(duration, asList(missions));
        return this;
    }


    @Override
    public MissionStrategy timesToRetry(Integer times, Mission... missions) {
        timesToRetry = times;
        return this;
    }

    @Override
    public MissionStrategy on(CherryEvent event, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy on(Class<? extends Throwable> throwableEvent, Mission... missions) {
        exceptionToMissions.put(throwableEvent, asList(missions));
        return this;
    }



    @Override
    public MissionStrategy onAnyException(Mission... missions) {
        catchAllExceptions = true;
        catchAllMissions = asList(missions);

        return this;
    }

    @Override
    public MissionStrategy onEvents(Set<CherryEvent> cherryEvents, Mission... missions) {
        return this;
    }



    @Override
    public MissionStrategy onExceptions(Set<Class<? extends Throwable>> events, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy first(Mission... missions) {
        this.beforeMissions = asList(missions);
        return this;
    }


    @Override
    public MissionStrategy onSuccess(Mission... missions) {
        this.afterMissions = asList(missions);
        return this;
    }


    @Override
    public MissionStrategy setNarrateExecutionTime(boolean shouldNarrateExecutionTime) {
        this.shouldNarrateExecutionTime = shouldNarrateExecutionTime;
        return this;
    }

    @Override
    public Boolean shouldNarrateExecutionTime() {
        return shouldNarrateExecutionTime;
    }

    @Override
    public int retries() {
        return timesToRetry;
    }

    public List<Mission> afterMissions() {
        return afterMissions;
    }
}

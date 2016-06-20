package io.magentys.cherry.reactive;

import akka.japi.Pair;
import io.magentys.Mission;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

public class MissionStrategy implements Eventful<MissionStrategy>{

    private List<Mission> beforeMissions = new ArrayList<>();
    private List<Mission> afterMissions = new ArrayList<>();
    private Pair<Duration,List<Mission>> durationToMissions = Pair.create(Duration.Zero(),new ArrayList<>());
    private Map<String, List<Mission>> eventToMissions = new ConcurrentHashMap<>();
    private Map<String, Class<? extends Throwable>> eventToExceptions  = new ConcurrentHashMap<>();
    private Map<Class<? extends Throwable>, Class<? extends Throwable>> exceptionToException  = new ConcurrentHashMap<>();
    private Map<Class<? extends Throwable>, Class<? extends Throwable>> exceptionToMissions  = new ConcurrentHashMap<>();
    private Integer timesToRetry = 0;
    private Boolean shouldNarrateExecutionTime = false;

    MissionStrategy(){}

    public static MissionStrategy aStrategy() { return new MissionStrategy(); }

    public List<Mission> beforeMissions() {
        return beforeMissions;
    }


    public Pair<Duration,List<Mission>> timeoutStrategy() {
        return durationToMissions;
    }

    public Map<String, List<Mission>> eventToMissions() {
        return eventToMissions;
    }

    public Map<String, Class<? extends Throwable>> eventToExceptions() {
        return eventToExceptions;
    }


    public Map<Class<? extends Throwable>, Class<? extends Throwable>> exceptionToExceptionMappings() {
        return exceptionToException;
    }

    public Map<Class<? extends Throwable>, Class<? extends Throwable>> exceptionToMissions() {
        return exceptionToMissions;
    }



    @Override
    public MissionStrategy on(Duration duration, Mission... missions) {
        durationToMissions = Pair.create(duration, asList(missions));
        return this;
    }


    @Override
    public MissionStrategy on(Duration duration, Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy timesToRetry(Integer times, Mission... missions) {
        timesToRetry = times;
        return this;
    }

    @Override
    public MissionStrategy timesToRetry(Integer times, Class<? extends Throwable> throwableClass) {
        timesToRetry = times;
        return this;
    }


    @Override
    public MissionStrategy on(MissionEvent event, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy on(MissionEvent event, Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy on(Class<? extends Throwable> throwableEvent, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy on(Class<? extends Throwable> throwableEvent, Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy onAnyException(Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy onAnyException(Mission... missions) {
        return this;
    }

    @Override
    public MissionStrategy onEvents(Set<MissionEvent> missionEvents, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy onEvents(Set<MissionEvent> missionEvents, Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy onExceptions(Set<Class<? extends Throwable>> events, Mission... missions) {
        return this;
    }


    @Override
    public MissionStrategy onExceptions(Set<Class<? extends Throwable>> events, Class<? extends Throwable> throwableClass) {
        return this;
    }

    @Override
    public MissionStrategy first(Mission... missions) {
        this.beforeMissions = asList(missions);
        return this;
    }


    @Override
    public MissionStrategy andFinally(Mission... missions) {
        this.afterMissions = asList(missions);
        return this;
    }


    @Override
    public MissionStrategy andFinally(Class<? extends Throwable> throwableClass) {
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

    public List<Mission> afterMissions() {
        return afterMissions;
    }
}

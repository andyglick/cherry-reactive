package io.magentys.cherry.reactive;

import io.magentys.Mission;
import scala.concurrent.duration.Duration;

import java.util.Set;


public interface Eventful<ReturnType extends Eventful> {

    ReturnType on(final Duration duration, final Mission... missions);
    ReturnType on(final Duration duration, final Class<? extends Throwable> throwableClass);

    ReturnType timesToRetry(final Integer times, final Mission... missions );
    ReturnType timesToRetry(final Integer times, final Class<? extends Throwable> throwableClass);

    ReturnType on(final MissionEvent event, final Mission... missions);
    ReturnType on(final MissionEvent event, final Class<? extends Throwable> throwableClass);

    ReturnType on(final Class<? extends Throwable> throwableEvent, final Mission... missions);
    ReturnType on(final Class<? extends Throwable> throwableEvent, final Class<? extends Throwable> throwableClass);


    ReturnType onAnyException(final Class<? extends Throwable> throwableClass);
    ReturnType onAnyException(Mission... missions);


    ReturnType onEvents(final Set<MissionEvent> missionEvents, final Mission... missions);
    ReturnType onEvents(final Set<MissionEvent> missionEvents, final Class<? extends Throwable> throwableClass);

    ReturnType onExceptions(final Set<Class<? extends Throwable>> events, final Mission... missions);
    ReturnType onExceptions(final Set<Class<? extends Throwable>> events, final Class<? extends Throwable> throwableClass);

    ReturnType butFirst(final Mission... missions);

    ReturnType andFinally(final Mission... missions);
    ReturnType andFinally(final Class<? extends Throwable> throwableClass);

    ReturnType setNarrateExecutionTime(final boolean shouldNarrateExecutionTime);
    Boolean shouldNarrateExecutionTime();
}

package io.magentys.cherry.reactive;

import io.magentys.Mission;
import io.magentys.cherry.reactive.events.CherryEvent;
import scala.concurrent.duration.Duration;

import java.util.Set;


public interface Eventful<ReturnType extends Eventful> {

    ReturnType on(final Duration duration, final Mission... missions);

    ReturnType timesToRetry(final Integer times, final Mission... missions );

    ReturnType on(final CherryEvent event, final Mission... missions);

    ReturnType on(final Class<? extends Throwable> throwableEvent, final Mission... missions);


    ReturnType onAnyException(Mission... missions);


    ReturnType onEvents(final Set<CherryEvent> cherryEvents, final Mission... missions);

    ReturnType onExceptions(final Set<Class<? extends Throwable>> events, final Mission... missions);

    ReturnType first(final Mission... missions);

    ReturnType andFinally(final Mission... missions);

    ReturnType setNarrateExecutionTime(final boolean shouldNarrateExecutionTime);

    Boolean shouldNarrateExecutionTime();

    int retries();
}

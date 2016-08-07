package io.magentys.cherry.reactive;

import io.magentys.Mission;
import io.magentys.cherry.reactive.events.CherryEvent;
import scala.concurrent.duration.FiniteDuration;

import java.util.Optional;
import java.util.Set;


@SuppressWarnings("WeakerAccess")
public abstract class BaseReactiveMission<RESULT> implements ReactiveMission<RESULT>
{

  private MissionStrategy missionStrategy;

  private MissionStrategy myStrategy()
  {
    if (missionStrategy == null)
    {
      missionStrategy = new MissionStrategy();
    }
    return missionStrategy;
  }

  @Override
  public ReactiveMission<RESULT> timeout(FiniteDuration duration, Mission... missions)
  {
    myStrategy().timeout(duration, missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> timesToRetry(Integer times, Mission... missions)
  {
    myStrategy().timesToRetry(times, missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> on(CherryEvent event, Mission... missions)
  {
    myStrategy().on(event, missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> on(Class<? extends Throwable> throwableEvent, Mission... missions)
  {
    myStrategy().on(throwableEvent, missions);
    return this;
  }


  public ReactiveMission<RESULT> onAnyException(Mission... missions)
  {
    myStrategy().onAnyException(missions);
    return this;
  }

  @Override
  public ReactiveMission<RESULT> onEvents(Set<CherryEvent> cherryEvents, Mission... missions)
  {
    myStrategy().onEvents(cherryEvents, missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> onExceptions(Set<Class<? extends Throwable>> events, Mission... missions)
  {
    myStrategy().onExceptions(events, missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> first(Mission... missions)
  {
    myStrategy().first(missions);
    return this;
  }


  @Override
  public ReactiveMission<RESULT> onSuccess(Mission... missions)
  {
    myStrategy().onSuccess(missions);
    return this;
  }

  @Override
  public ReactiveMission<RESULT> withStrategy(MissionStrategy missionStrategy)
  {
    this.missionStrategy = missionStrategy;
    return this;
  }

  @Override
  public Optional<MissionStrategy> strategy()
  {
    return Optional.ofNullable(missionStrategy);
  }

  @Override
  public Boolean hasStrategy()
  {
    return missionStrategy != null;
  }

  @Override
  public ReactiveMission<RESULT> setNarrateExecutionTime(final boolean shouldNarrateExecutionTime)
  {
    myStrategy().setNarrateExecutionTime(shouldNarrateExecutionTime);
    return this;
  }

  @Override
  public int retries()
  {
    return myStrategy().retries();
  }

  @Override
  public Boolean shouldNarrateExecutionTime()
  {
    return myStrategy().shouldNarrateExecutionTime();
  }


}

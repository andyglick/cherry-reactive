package io.magentys.cherry.reactive;

import io.magentys.Mission;

import java.util.Optional;

/**
 * Provides ability to have an execution strategy
 *
 * @param <RESULT>
 */
public interface ReactiveMission<RESULT> extends Mission<RESULT>, Eventful<ReactiveMission<RESULT>>
{
  ReactiveMission<RESULT> withStrategy(final MissionStrategy missionStrategy);

  Optional<MissionStrategy> strategy();

  Boolean hasStrategy();

  String name();
}

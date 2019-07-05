package io.magentys.cherry.reactive;

import org.junit.Assert;
import org.junit.Test;

import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.Mission;
import io.magentys.annotations.Narrate;
import io.magentys.cherry.reactive.common.Either;
import io.magentys.cherry.reactive.models.Failure;
import io.magentys.java8.FunctionalAgentProvider;
import io.magentys.narrators.SysoutNarrator;
import scala.concurrent.duration.Duration;

import static io.magentys.CoreMemory.coreMemory;
import static io.magentys.cherry.reactive.MissionStrategy.aStrategy;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThat.doThat;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThis.doThis;
import static io.magentys.cherry.reactive.ReactiveAgentTest.Print.printSuccess;
import static io.magentys.cherry.reactive.ReactiveAgentTest.TakeScreenshot.takeScreenshot;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings({"WeakerAccess", "unused", "ThrowableResultOfMethodCallIgnored", "unchecked"})
public class ReactiveAgentTest
{
  private static final String VALUE = "value";

  @Test
  public void shouldCreateReactiveAgentWithMemory() throws Exception
  {
    CoreMemory memory = coreMemory();
    memory.remember("test", VALUE);
    ReactiveAgent reactiveAgent = ReactiveAgent.create(memory);
    assertThat(reactiveAgent.getMemory()
      .isEmpty())
      .as("memory is not empty")
      .isFalse();

    assertThat(reactiveAgent.getMemory()
      .recall("test")
      .get())
      .as("recall that the key \"test\" produces " + VALUE).isEqualTo(VALUE);
  }

  @Test
  public void shouldCreateReactiveAgentFromExistingAgent() throws Exception
  {
    Agent agent = FunctionalAgentProvider.agent();
    agent.keepsInMind("test", "value");
    agent.obtains(new Tool());
    ReactiveAgent reactiveAgent = ReactiveAgent.createFrom(agent);

    assertThat(reactiveAgent.recalls("test", String.class))
      .as(" \"value\" is what is being returned ")
      .isEqualTo("value");

    assertThat(reactiveAgent.getTools()
      .size())
      .as("size must be 1")
      .isEqualTo(1);
  }

  @Test
  public void shouldPerformMissionSuccessfully() throws Throwable
  {
    ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory())
      .withDefaultEventStrategy(
        aStrategy().first(doThis())
          .onAnyException(doThat()));

    reactiveAgent.addNarrators(new SysoutNarrator());
    SuccessfulMission successfulMission = new SuccessfulMission();
    reactiveAgent.obtains(new Tool());
    reactiveAgent.performsReactively(
      successfulMission
        .first(doThat(), doThat(), doThis())
        .timeout(Duration.create(3, MINUTES), doThis())
        .on(RuntimeException.class, doThis())
        .onSuccess(printSuccess(), printSuccess())
    );

    assertThat(reactiveAgent.hasFailed())
      .as("the agent has succeeded")
      .isFalse();

    assertThat(reactiveAgent.hasFailed()).isFalse();
    assertThat(reactiveAgent.getFailure()).isEqualTo(Failure.empty());
    reactiveAgent.terminate();
  }

  @Test
  public void shouldMarkAgentAsFailedInCaseOfAnError() throws Throwable
  {
    ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory(), "ReactiveAgent1")
      .withDefaultEventStrategy(
        aStrategy().first(doThat())
          .onAnyException(doThis()));

    reactiveAgent.obtains(new Tool());
    reactiveAgent.addNarrators(new SysoutNarrator());
    DangerousMission dangerousMission = new DangerousMission();
    Either<String, Failure> result = reactiveAgent.performsReactively(dangerousMission
        .first(doThat(), doThat(), doThis())
        .timeout(Duration.create(3, SECONDS), doThis())
        .on(RuntimeException.class, takeScreenshot())
        .onSuccess(printSuccess())
    );
    Assert.assertThat(reactiveAgent.hasFailed(), is(true));
    Assert.assertThat(reactiveAgent.getFailureAs(Exception.class).getMessage(), is("Futures timed out after [3 seconds]"));
    reactiveAgent.terminate();
  }

  @Test
  public void shouldMarkAgentAsFailedInCaseOfAnErrorUsingTheDefaultStrategy() throws Exception
  {
    ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory(), "ReactiveAgent1")
      .withDefaultEventStrategy(aStrategy().on(RuntimeException.class, takeScreenshot()));

    reactiveAgent.obtains(new Tool());
    reactiveAgent.addNarrators(new SysoutNarrator());
    DangerousMission dangerousMission = new DangerousMission();
    reactiveAgent.performsReactively(dangerousMission);
    Assert.assertThat(reactiveAgent.hasFailed(), is(true));
    reactiveAgent.terminate();
  }

  private class Tool
  {
    String getName()
    {
      return "test";
    }
  }

  @Narrate("My Successful mission")
  static class SuccessfulMission extends BaseReactiveMission<String>
  {

    @Override
    public String accomplishAs(Agent agent)
    {
      return agent.usingThe(Tool.class).getName();
    }

    @Override
    public String name()
    {
      return "SuccessfulMission";
    }
  }

  @Narrate("My Failed mission")
  static class DangerousMission extends BaseReactiveMission<String>
  {

    @Override
    public String accomplishAs(Agent agent)
    {
      throw new RuntimeException("aaaaaaaaaah!");
    }

    @Override
    public String name()
    {
      return "SuccessfulMission";
    }
  }


  @Narrate("I do this!")
  static class DoThis implements Mission<Agent>
  {

    static DoThis doThis()
    {
      return new DoThis();
    }

    @Override
    public Agent accomplishAs(Agent agent)
    {
      return agent;
    }
  }

  @Narrate("I do that!")
  static class DoThat implements Mission<Agent>
  {

    static DoThat doThat()
    {
      return new DoThat();
    }

    @Override
    public Agent accomplishAs(Agent agent)
    {
      return agent;
    }
  }

  @Narrate("Success!")
  static class Print implements Mission<Agent>
  {

    static Print printSuccess()
    {
      return new Print();
    }

    @Override
    public Agent accomplishAs(Agent agent)
    {
      return agent;
    }
  }

  @Narrate("I take screenshots!...")
  static class TakeScreenshot implements Mission<Agent>
  {

    static TakeScreenshot takeScreenshot()
    {
      return new TakeScreenshot();
    }

    @Override
    public Agent accomplishAs(Agent agent)
    {
      return agent;
    }
  }
}

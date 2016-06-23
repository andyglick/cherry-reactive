package io.magentys.cherry.reactive;

import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.Mission;
import io.magentys.annotations.Narrate;
import io.magentys.cherry.reactive.models.Failure;
import io.magentys.java8.FunctionalAgentProvider;
import io.magentys.narrators.SysoutNarrator;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import static io.magentys.CoreMemory.coreMemory;
import static io.magentys.cherry.reactive.MissionStrategy.aStrategy;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThat.doThat;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThis.doThis;
import static io.magentys.cherry.reactive.ReactiveAgentTest.TakeScreenshot.takeScreenshot;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReactiveAgentTest {



    @Test
    public void shouldCreateReactiveAgentWithMemory() throws Exception {
        CoreMemory memory = coreMemory();
        memory.remember("test", "value");
        ReactiveAgent reactiveAgent = ReactiveAgent.create(memory);
        assertThat(reactiveAgent.getMemory().isEmpty(), is(false));
        assertThat(reactiveAgent.getMemory().recall("test").get(), is("value"));
    }

    @Test
    public void shouldCreateReactiveAgentFromExistingAgent() throws Exception {
        Agent agent = FunctionalAgentProvider.agent();
        agent.keepsInMind("test","value");
        agent.obtains(new Tool());
        ReactiveAgent reactiveAgent = ReactiveAgent.createFrom(agent);
        assertThat(reactiveAgent.recalls("test", String.class), is("value"));
        assertThat(reactiveAgent.getTools().size(), is(1));
    }

    @Test
    public void shouldPerformMissionSuccessfully() throws Exception {
        ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory())
                                                   .withDefaultStrategy(
                                                                        aStrategy().first(doThat())
                                                                                   .onAnyException(doThis()));


        reactiveAgent.addNarrators(new SysoutNarrator());
        SecretMission secretMission = new SecretMission();
        reactiveAgent.obtains(new Tool());
        reactiveAgent.performsReactively(
                secretMission

                        .first(doThat(), doThat(), doThis())
                        .timeout(Duration.create(3, MINUTES), doThis())
                        .on(RuntimeException.class, doThis())
                        .onSuccess(doThat(), doThis())
        );
        assertThat(reactiveAgent.hasFailed(), is(false));
        assertThat(reactiveAgent.getFailure(),is(Failure.empty()));
        reactiveAgent.terminate();

    }

    @Test
    public void shouldMarkAgentAsFailedInCaseOfAnError() throws Exception {
        ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory(), "ReactiveAgent1")
                                                   .withDefaultStrategy(
                                                                        aStrategy().first(doThat())
                                                                                   .onAnyException(doThis()));

        reactiveAgent.obtains(new Tool());
        reactiveAgent.addNarrators(new SysoutNarrator());
        DangerousMission dangerousMission = new DangerousMission();
        String result = reactiveAgent.performsReactively(
                dangerousMission

                                 .first(doThat(), doThat(), doThis())
                                 .timeout(Duration.create(3, SECONDS), doThis())
                                 .on(RuntimeException.class, takeScreenshot())
                                 .onSuccess(
                                         doThat(),
                                         doThis())
                        );
        assertThat(reactiveAgent.hasFailed(), is(true));
        assertThat(reactiveAgent.getFailureAs(Exception.class).getMessage(), is("Futures timed out after [1 second]"));
        reactiveAgent.terminate();

    }

    private class Tool{
        public String getName() {
            return "test";
        }
    }

    @Narrate("My Secret mission")
    private static class SecretMission extends BaseReactiveMission<String> {

        @Override
        public String accomplishAs(Agent agent) {
            Tool tool = agent.usingThe(Tool.class);
            return tool.getName();
        }

        @Override
        public String name() {
            return "SecretMission";
        }
    }

    @Narrate("My Failed mission")
    private static class DangerousMission extends BaseReactiveMission<String> {

        @Override
        public String accomplishAs(Agent agent) {
           throw new RuntimeException("aaaaaaaaaa!");
        }

        @Override
        public String name() {
            return "SecretMission";
        }
    }



    @Narrate("I do this!")
    public static class DoThis implements Mission<Agent> {

        public static DoThis doThis() { return new DoThis(); }

        @Override
        public Agent accomplishAs(Agent agent) {
            return agent;
        }
    }

    @Narrate("I do that!")
    public static class DoThat implements Mission<Agent> {

        public static DoThat doThat() { return new DoThat(); }

        @Override
        public Agent accomplishAs(Agent agent) {
            return agent;
        }
    }

    @Narrate("I take screenshots!...")
    public static class TakeScreenshot implements Mission<Agent>{

        public static TakeScreenshot takeScreenshot() { return new TakeScreenshot(); }

        @Override
        public Agent accomplishAs(Agent agent) {
            return agent;
        }
    }
}

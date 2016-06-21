package io.magentys.cherry.reactive;

import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.Mission;
import io.magentys.annotations.Narrate;
import io.magentys.java8.FunctionalAgentProvider;
import io.magentys.narrators.SysoutNarrator;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import static io.magentys.CoreMemory.coreMemory;
import static io.magentys.cherry.reactive.MissionStrategy.aStrategy;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThat.doThat;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThis.doThis;
import static io.magentys.cherry.reactive.MultiMissionEvent.eventOf;
import static java.util.concurrent.TimeUnit.MINUTES;
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
        reactiveAgent.performsReactively(
                secretMission

                                 .first(doThat(), doThat(), doThis())
                                 .on(Duration.create(3, MINUTES), doThis())
                                 .on(RuntimeException.class, doThis())
                                 .andFinally(doThat(), doThis())
                        );
        assertThat(reactiveAgent.hasFailed(),is(false));
        reactiveAgent.terminate();

    }

    @Test
    public void shouldPerformMissionUnuccessfully() throws Exception {
        ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory())
                                                   .withDefaultStrategy(
                                                                        aStrategy().first(doThat())
                                                                                   .onAnyException(doThis()));

        reactiveAgent.obtains(new Tool());
        reactiveAgent.addNarrators(new SysoutNarrator());
        SecretMission secretMission = new SecretMission();
        reactiveAgent.performsReactively(
                secretMission

                                 .first(doThat(), doThat(), doThis())
                                 .on(Duration.create(3, MINUTES), doThis())
                                 .on(RuntimeException.class, doThis())
                                 .andFinally(doThat(), doThis())
                        );
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
}

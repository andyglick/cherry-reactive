package io.magentys.cherry.reactive;

import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.Mission;
import io.magentys.annotations.Narrate;
import io.magentys.java8.FunctionalAgentProvider;
import io.magentys.narrators.SysoutNarrator;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static io.magentys.CoreMemory.coreMemory;
import static io.magentys.cherry.reactive.MissionStrategy.aStrategy;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThat.doThat;
import static io.magentys.cherry.reactive.ReactiveAgentTest.DoThis.doThis;
import static io.magentys.cherry.reactive.SimpleEvent.eventOf;
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
    public void shouldDoSomething() throws Exception {
        ReactiveAgent reactiveAgent = ReactiveAgent.create(coreMemory())
                                                   .withDefaultStrategy(
                                                                        aStrategy().butFirst(doThis(), doThat())
                                                                                   .onAnyException(RuntimeException.class));

        reactiveAgent.addNarrators(new SysoutNarrator());
        SecretMission secretMission = new SecretMission();
        reactiveAgent.performs(
                secretMission
                                 .butFirst(doThis())
                                 .on(Duration.create(3, MINUTES), doThis())
                                 .on(eventOf("TimeOut"), doThis())
                                 .on(eventOf("Event2"), RuntimeException.class)
                                 .on(RuntimeException.class, doThis())
                                 .andFinally(doThat(), doThis())
                        );

    }

    private class Tool{}

    @Narrate("My Mystical mission")
    private static class SecretMission extends BaseReactiveMission<String> {

        @Override
        public String accomplishAs(Agent agent) {
            return "test";
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

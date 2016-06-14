package io.magentys.cherry.reactive;

import io.magentys.Agent;
import io.magentys.CoreMemory;
import io.magentys.java8.FunctionalAgentProvider;
import org.junit.Test;

import static io.magentys.CoreMemory.coreMemory;
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

    private class Tool{}
}

package io.magentys.cherry.reactive;

import akka.actor.ActorSystem;
import io.magentys.Agent;
import io.magentys.Memory;
import io.magentys.java8.FunctionalAgent;


public class ReactiveAgent extends FunctionalAgent {

    static ActorSystem system;

    private Supervisor supervisor;

    public ReactiveAgent(Memory memory) {
        super(memory);
        system = ActorSystem.create(this.name());
    }

    public static ReactiveAgent create(Memory memory){
        return new ReactiveAgent(memory);
    }

    public static ReactiveAgent createFrom(Agent agent){
        return (ReactiveAgent) new ReactiveAgent(agent.getMemory()).setTools(agent.getTools());
    }


}

package io.magentys.cherry.reactive;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class Supervisor extends AbstractActor {

    public Supervisor() {
        receive(ReceiveBuilder.
                match(Props.class, props -> sender().tell(context().actorOf(props), self())).build()
        );
    }


}

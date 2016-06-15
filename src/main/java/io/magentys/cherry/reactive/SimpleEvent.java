package io.magentys.cherry.reactive;

public class SimpleEvent implements MissionEvent<String,String>{

    private final String name;
    private final String body;
    private final String metadata;

    public SimpleEvent(final String name) {
        this.name = name;
        this.body = "";
        this.metadata = "";
    }

    public SimpleEvent(final String name, final String body) {
        this.name = name;
        this.body = body;
        this.metadata = "";
    }

    public SimpleEvent(final String name, final String body, final String metadata) {
        this.name = name;
        this.body = body;
        this.metadata = metadata;
    }

    public static SimpleEvent eventOf(final String name){
        return new SimpleEvent(name);
    }

    public static SimpleEvent eventOf(final String name, final String body){
        return new SimpleEvent(name, body);
    }

    public static SimpleEvent eventOf(final String name, final String body, final String metadata){
        return new SimpleEvent(name, body, metadata);
    }


    @Override
    public String body() {
        return body;
    }


    @Override
    public String name() {
        return name;
    }


    @Override
    public String metadata() {
        return metadata;
    }


}

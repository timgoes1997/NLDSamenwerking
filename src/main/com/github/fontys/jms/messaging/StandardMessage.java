package com.github.fontys.jms.messaging;

public class StandardMessage<OBJECT> {

    private OBJECT object;

    public StandardMessage(OBJECT object) {
        this.object = object;
    }

    public OBJECT getObject() {
        return object;
    }

    @Override
    public String toString() {
        return object.toString();
    }

}

package com.github.fontys.jms.listeners;

import com.github.fontys.jms.messaging.RequestReply;

import javax.jms.JMSException;

public interface ClientInterface {
    void receivedAction(RequestReply requestReply) throws JMSException;
}

package com.github.fontys.jms.gateway;

import com.github.fontys.jms.listeners.ClientInterface;
import com.github.fontys.jms.messaging.RequestReply;
import com.github.fontys.jms.serializer.Serializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;

public class AppGateWay<REQUEST, REPLY> {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiverGateway;
    private Serializer serializer;
    private ClientInterface clientInterface;

    private final Class<REQUEST> requestClass;
    private final Class<REPLY> replyClass;

    public AppGateWay(ClientInterface clientInterface, String senderChannel, String receiverChannel, String provider, Class<REQUEST> requestClass, Class<REPLY> replyClass) throws JMSException, NamingException {
        this.sender = new MessageSenderGateway(senderChannel, provider);
        this.requestClass = requestClass;
        this.replyClass = replyClass;
        this.serializer = new Serializer<REQUEST, REPLY>(requestClass, replyClass);
        this.receiverGateway = new MessageReceiverGateway(receiverChannel, provider);
        this.clientInterface = clientInterface;
        this.receiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    onReplyArrived(serializer.requestReplyFromString(((TextMessage) message).getText()));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(RequestReply rr) throws JMSException {
        sender.send(sender.createTextMessage(serializer.requestReplyToString(rr)));
    }

    public void onReplyArrived(RequestReply rr) throws JMSException {
        if(rr != null) {
            clientInterface.receivedAction(rr);
        }else{
            throw new JMSException("Received a message with a null value");
        }
    }
}

package com.github.fontys.jms.gateway.requestreply;

import com.github.fontys.jms.listeners.ClientInterfaceRequestReply;
import com.github.fontys.jms.messaging.RequestReply;
import com.github.fontys.jms.serializer.RequestReplySerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;

public class RequestReplyGateWay<REQUEST, REPLY> {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiverGateway;
    private RequestReplySerializer serializer;
    private ClientInterfaceRequestReply clientInterface;

    private final Class<REQUEST> requestClass;
    private final Class<REPLY> replyClass;

    public RequestReplyGateWay(ClientInterfaceRequestReply clientInterface, String senderChannel, String receiverChannel, String provider, Class<REQUEST> requestClass, Class<REPLY> replyClass) throws JMSException, NamingException {
        this.sender = new MessageSenderGateway(senderChannel, provider);
        this.requestClass = requestClass;
        this.replyClass = replyClass;
        this.serializer = new RequestReplySerializer<REQUEST, REPLY>(requestClass, replyClass);
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

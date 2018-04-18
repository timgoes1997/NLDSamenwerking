package com.github.fontys.jms.gateway.object;

import com.github.fontys.jms.gateway.requestreply.MessageReceiverGateway;
import com.github.fontys.jms.gateway.requestreply.MessageSenderGateway;
import com.github.fontys.jms.listeners.ClientInterfaceObject;
import com.github.fontys.jms.listeners.ClientInterfaceRequestReply;
import com.github.fontys.jms.messaging.RequestReply;
import com.github.fontys.jms.messaging.StandardMessage;
import com.github.fontys.jms.serializer.ObjectSerializer;
import com.github.fontys.jms.serializer.RequestReplySerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;

public class ObjectGateway<OBJECT> {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiverGateway;
    private ObjectSerializer serializer;
    private ClientInterfaceObject clientInterface;

    private final Class<OBJECT> objectClass;

    public ObjectGateway(ClientInterfaceObject clientInterface, String senderChannel, String receiverChannel, String provider, Class<OBJECT> objectClass) throws JMSException, NamingException {
        this.sender = new MessageSenderGateway(senderChannel, provider);
        this.objectClass = objectClass;
        this.serializer = new ObjectSerializer(objectClass);
        this.receiverGateway = new MessageReceiverGateway(receiverChannel, provider);
        this.clientInterface = clientInterface;
        this.receiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    onReplyArrived(serializer.standardMessageFromString(((TextMessage) message).getText()));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(StandardMessage sm) throws JMSException {
        sender.send(sender.createTextMessage(serializer.standardMessageToString(sm)));
    }

    public void onReplyArrived(StandardMessage sm) throws JMSException {
        if(sm != null) {
            clientInterface.receivedAction(sm);
        }else{
            throw new JMSException("Received a message with a null value");
        }
    }
}

package com.github.fontys.beans;

import com.github.fontys.constant.Constant;
import com.github.fontys.jms.gateway.object.ObjectGateway;
import com.github.fontys.jms.listeners.ClientInterfaceObject;
import com.github.fontys.jms.messaging.RequestReply;
import com.github.fontys.jms.messaging.StandardMessage;
import com.github.fontys.jms.object.VehicleObject;
import com.github.fontys.jms.reply.VehicleReply;
import com.github.fontys.jms.request.VehicleRequest;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

@Path("/dutch")
@Stateless
public class DutchBean {

    private ObjectGateway dutchGateway;

    @Inject
    private GermanBean germanBean;

    @PostConstruct
    public void init(){
        try {
            this.dutchGateway = new ObjectGateway(new ClientInterfaceObject() {
                @Override
                public void receivedAction(StandardMessage standardMessage) throws JMSException {
                    germanBean.getGermanGateway().send(standardMessage);
                }
            }, Constant.DUTCH_SENDERCHANNEL, Constant.DUTCH_RECEIVERCHANNEL, Constant.PROVIDER, VehicleObject.class);
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    public ObjectGateway getDutchGateway() {
        return dutchGateway;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/car/arrival/{license}")
    public Response onCarArrival(@PathParam("license") String license){
        return Response.ok().build();
    }


}

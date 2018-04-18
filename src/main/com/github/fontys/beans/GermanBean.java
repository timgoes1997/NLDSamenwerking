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
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/german")
@Startup
@Stateless
public class GermanBean {

    private ObjectGateway germanGateway;

    @Inject
    private DutchBean dutchBean;

    @PostConstruct
    public void init(){
        try {
            this.germanGateway = new ObjectGateway(new ClientInterfaceObject() {

                @Override
                public void receivedAction(StandardMessage standardMessage) throws JMSException {
                    dutchBean.getDutchGateway().send(standardMessage);
                }
            }, Constant.GERMAN_SENDERCHANNEL, Constant.GERMAN_RECEIVERCHANNEL, Constant.PROVIDER, VehicleObject.class);
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }

    public ObjectGateway getGermanGateway() {
        return germanGateway;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/car/arrival/{license}")
    public Response onCarArrival(@PathParam("license") String license){
        System.out.println("Car arrived");
        return Response.ok().build();
    }

}

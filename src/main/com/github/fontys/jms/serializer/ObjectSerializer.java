package com.github.fontys.jms.serializer;

import com.github.fontys.jms.messaging.RequestReply;
import com.github.fontys.jms.messaging.StandardMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ObjectSerializer<OBJECT> {
    private Gson gson; //gebruikte eerst genson maar die heeft slechte ondersteuning voor generics zoals RequestReply, terwijl gson dit met gemak ondersteund

    private final Class<OBJECT> objectClass;

    public ObjectSerializer(Class<OBJECT> objectClass){
        this.objectClass = objectClass;
        gson = new GsonBuilder().create();
    }

    public String objectToString(OBJECT object){
        return gson.toJson(object);
    }

    public OBJECT objectFromString(String str){
        return gson.fromJson(str, objectClass);
    }

    public StandardMessage standardMessageFromString(String str){
        return gson.fromJson(str, TypeToken.getParameterized(RequestReply.class, objectClass).getType());
    }

    public String standardMessageToString(StandardMessage standardMessage){
        return gson.toJson(standardMessage);
    }
}

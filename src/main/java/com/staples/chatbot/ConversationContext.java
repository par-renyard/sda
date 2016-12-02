package com.staples.chatbot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by benquintana on 11/17/16.
 *
 */
public class ConversationContext {

    private Map<String,Object> context = new HashMap<>();

    public int getInt(String name){
        if(context.get(name)==null){
            context.put(name,0);
        }

        try {
            return (Integer)context.get(name);
        }catch (Exception e){
            return -1;
        }
    }

    public String getString(String name){
        try {
            return (String) context.get(name);
        }catch (Exception e){
            return null;
        }
    }

    public void increment(String name){
        this.context.put(name,getInt(name)+1);
    }

    public Object get(String name){
        return context.get(name);
    }

    public void set(String name,Object data){
        this.context.put(name,data);
    }

    public Map asMap() {
        return this.context;
    }
}

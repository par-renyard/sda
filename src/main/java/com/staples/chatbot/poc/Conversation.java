package com.staples.chatbot.poc;

import com.staples.chatbot.ConversationContext;
import com.staples.chatbot.IntentClient;
import com.staples.chatbot.IntentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by benquintana on 11/17/16.
 *
 * Rules representing a specific conversation.
 * Basically just a list of conversation states and the current conversation
 *
 */
public class Conversation {

    private static final Logger Log = LoggerFactory.getLogger(Conversation.class);

    Map<String,ConversationState> states = new HashMap<>();

    private ConsoleClient channel;

    private ConversationContext context = new ConversationContext();
    private static IntentClient client = new IntentClient();

    private ConversationState currentState;

    public Conversation(ConsoleClient channel){
        this.channel=channel;
        init();
        client.init();
    }

    public ConsoleClient getChannel() {
        return channel;
    }

    private void init() {


        addState(new ConversationState("Greeting",this));
        addState(new ConversationState("CaptureOrder",this));
        addState(new ConversationState("VerifyOrder",this));
        addState(new ConversationState("CaptureZip",this));
        addState(new ConversationState("HaveValidOrder",this));
        addState(new ConversationState("LookupOrderNum",this));
        addState(new ConversationState("AnythingElse",this));
        addState(new ConversationState("VerifyWiMS",this));

//
//        addState(new CaptureOrderState("CaptureOrder",this));
//        addState(new VerifyOrderNumberState("VerifyOrder",this));
//        addState(new GreetingState("Greeting",this));
//        addState(new CaptureZipState("CaptureZip",this));

        this.currentState=states.get("Greeting");



    }

    private void addState(ConversationState state){
        this.states.put(state.getName(),state);
    }

    public ConversationContext getContext() {
        return context;
    }

    public IntentClient getCognitiveClient() {
        return client;
    }

    boolean started = false;
    public void receiveUtterance(String message) {
        if(!started){
            start(message);
            this.started=true;
        }else {
            this.currentState.receiveUtterance(message);
        }

    }

    public IntentResponse getCurrentIntent() {
        return this.currentState.getCurrentIntent();
    }

    public void start(String message) {
        this.currentState.enterContext(message);
    }

    public void changeState(String state) {
        Log.info("Changing state to:  {}",state);
        this.currentState=this.states.get(state);
        this.currentState.enterContext(null);
    }
}

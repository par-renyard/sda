package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by benquintana on 11/20/16.
 *
 */
public class SayAction implements Action {
    private static final Logger Log = LoggerFactory.getLogger(SayAction.class);


    private String message;

    public SayAction(String msg){
        this.message=msg;
    }
    @Override
    public void execute(ConversationState state) {
        Log.debug("Conversation state [{}] saying [{}]",state.getName(),message);
        state.getChannel().sendMessage(message);
    }
}

package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by benquintana on 11/20/16.
 *
 * Transfer the chat to an agent.
 *
 */
public class ChangeStateAction implements Action {
    private static final Logger Log = LoggerFactory.getLogger(ChangeStateAction.class);

    private String newState;

    public ChangeStateAction(String state){
        this.newState=state;
    }
    @Override
    public void execute(ConversationState state) {
        Log.debug("Conversation state [{}] changing to state [{}]",state.getName(),newState);

        state.getConversation().changeState(newState);
    }
}

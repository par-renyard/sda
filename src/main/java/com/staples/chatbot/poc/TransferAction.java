package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by benquintana on 11/20/16.
 *
 * Transfer the chat to an agent.
 *
 */
public class TransferAction implements Action {
    private static final Logger Log = LoggerFactory.getLogger(TransferAction.class);

    private String message;

    public TransferAction(String msg){
        this.message=msg;
    }
    @Override
    public void execute(ConversationState state) {
        Log.debug("Conversation state [{}] transfering to agent with message [{}]",state.getName(),message);
        if(message!=null&&message.trim().length()>0){
            state.getChannel().sendMessage(message);
        }
        state.getChannel().transfer();
    }
}

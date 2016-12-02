package com.staples.chatbot.poc;

/**
 * Created by benquintana on 11/19/16.
 *
 */
public interface Action {

    void execute(ConversationState state);
}

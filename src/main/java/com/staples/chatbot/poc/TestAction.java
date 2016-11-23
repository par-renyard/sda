package com.staples.chatbot.poc;

/**
 * Created by benquintana on 11/20/16.
 */
public class TestAction implements Action {
    @Override
    public void execute(ConversationState state) {
        System.out.println("Excecuting a custom action!");
    }
}

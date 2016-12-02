package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benquintana on 11/20/16.
 *
 * Custom rule to extract the order number from the utterance
 *
 */
public class ExtractOrderNumRule extends Rule {

    private static final Logger Log = LoggerFactory.getLogger(ExtractOrderNumRule.class);

    @Override
    protected void init() {
        addCheck(new Check() {
            @Override
            public boolean evaluate(ConversationState state) {

                String msg = state.getCurrentUtterance();
                Log.debug("Trying to extract order number from msg {}",msg);

                Pattern regex = Pattern.compile("(\\d{10})");
                Matcher regexMatcher = regex.matcher(msg);
                if(regexMatcher.find()){
                    String order = regexMatcher.group(1);
                    Log.debug("Found order number as [{}]",order);
                    state.getConversation().getContext().set("orderNumber",order);
                    return true;
                }
                return false;
            }
        });

        addTrueAction(new Action() {
            @Override
            public void execute(ConversationState state) {
                state.getChannel().sendMessage("Great I have your order number as "+state.getConversation().getContext().get("orderNumber"));
                state.getConversation().changeState("VerifyOrder");
            }
        });


    }
}

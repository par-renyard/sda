package com.staples.chatbot.poc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benquintana on 11/20/16.
 *
 */
public class ConversationRules {


    private String stateName;

    protected List<Rule> intentRules = new ArrayList<>();
    protected List<Rule> entryRules = new ArrayList<>();
    protected List<Rule> defaultRules = new ArrayList<>();

    public String getStateName() {
        return stateName;
    }

    public List<Rule> getIntentRules() {
        return intentRules;
    }

    public List<Rule> getEntryRules() {
        return entryRules;
    }

    public List<Rule> getDefaultRules() {
        return defaultRules;
    }

    public void setDefaultRules(List<Rule> defaultRules) {
        this.defaultRules = defaultRules;
    }

    public ConversationRules addIntentRule(Rule rule) {
        return addRule(intentRules,rule);
    }

    public ConversationRules addEntryRule(Rule rule) {
        return addRule(entryRules,rule);

    }

    public ConversationRules addDefaultRule(Rule rule) {
        return addRule(defaultRules,rule);
    }

    private ConversationRules addRule(List<Rule> list,Rule rule){
        list.add(rule);
        return this;
    }

//    public static void main(String[] args) {
//        ConversationRules rules = new ConversationRules();
//        rules.addEntryRule(new IntentCheck(Intent.Greeting,.85))
//                .addEntryRule()
//    }

}

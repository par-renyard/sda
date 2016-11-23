package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benquintana on 11/19/16.
 *
 */
public class Rule implements Action {

    private static final Logger Log = LoggerFactory.getLogger(Rule.class);

    List<Check> checks = new ArrayList<>();

    List<Action> trueActions = new ArrayList<>();

    List<Action> falseActions = new ArrayList<>();

    private String name;

    public Rule() {
        init();
    }

    public boolean evaluate(ConversationState state){
        Log.debug("Checking rule [{}]",name);

        for(Check check:checks){
            if(!check.evaluate(state)){
                Log.debug("Rule [{}] checked false",name);
                return false;
            }
        }

        Log.debug("Rule [{}] checked true",name);
        return true;
    }

    public List<Action> getTrueActions() {
        return trueActions;
    }

    public List<Action> getFalseActions() {
        return falseActions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rule addTrueAction(Action action){
        this.trueActions.add(action);
        return this;
    }

    public Rule addFalseAction(Action action){
        this.falseActions.add(action);
        return this;
    }

    public Rule addCheck(Check check){
        this.checks.add(check);
        return this;
    }

    @Override
    public void execute(ConversationState state) {
        Log.debug("Executing rule [{}]",getName());
        if(evaluate(state)){
            Log.debug("Executing true actions...");
            for(Action action:trueActions){
                action.execute(state);
            }
        }else {
            Log.debug("Executing false actions...");
            for(Action action:falseActions){
                action.execute(state);
            }
        }
    }

    /**
     * Child classes can extend this method to create custom rule checks
     */
    protected void init() {

    }
}

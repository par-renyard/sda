package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by benquintana on 11/19/16.
 *
 * A JavaScript based action which uses nashorn
 *
 * Can check and set any values in the state context...
 *
 */
public class ScriptAction implements Action {

    private static final Logger Log = LoggerFactory.getLogger(ScriptAction.class);

    private Invocable invocable = null;

    private String script;

    public ScriptAction(String script){
        this.script=script;
        init(script);
    }

    @Override
    public void execute(ConversationState state) {
        Log.debug("Attempting to execute script [{}]",script);

        try {
            Object result = invocable.invokeFunction("doIt", state.getContext().asMap());
            Log.debug("Script returned [{}]",result);
        }catch (Exception e){
            Log.error("Error executing script action",e);
        }

    }

    private void init(String script) {

        Log.info("Initializing script check.  Script [{}]",script);


        try {
            String js =
                    "var doIt = function(context) { return "+ script +";};";

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(js);

            this.invocable = (Invocable) engine;

        }catch (Exception e){
            Log.error("Error initializing script!",e);
        }

    }

    public static void main(String[] args) {
        ScriptAction check = new ScriptAction("context.tryCount++");

        ConversationState state = new ConversationState("Test",new Conversation(null)) {
            @Override
            protected void init() {

            }
        };

        System.out.println(state.getContext().get("tryCount"));

        check.execute(state);

        System.out.println(state.getContext().get("tryCount"));


    }

}

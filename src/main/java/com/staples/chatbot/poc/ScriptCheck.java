package com.staples.chatbot.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by benquintana on 11/19/16.
 *
 * A JavaScript based rule check which uses nashorn
 *
 * You can configure a simple boolean check which will be evaluated against the conversation context.
 *
 */
public class ScriptCheck implements Check {

    private static final Logger Log = LoggerFactory.getLogger(ScriptCheck.class);

    private Invocable invocable = null;

    private String script;

    public ScriptCheck(String script){
        this.script=script;
        init(script);
    }

    @Override
    public boolean evaluate(ConversationState state) {

        Log.debug("Attempting to execute script [{}]",script);

        try {
            Object result = invocable.invokeFunction("checkIt",
                    state.getContext().asMap(),
                    state.getConversation().getContext().asMap());
            if(result==null){
                Log.warn("Weird the script returned null!");
                return false;
            }
            Log.debug("Script returned [{}]",result);
            return (Boolean)result;
        }catch (Exception e){
            Log.error("Error executing script check",e);
            return false;
        }

    }

    private void init(String script) {

        Log.info("Initializing script check.  Script [{}]",script);


        try {
            String js =
                    "var checkIt = function(context,gcontext) { return "+ script +"};";

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(js);

            this.invocable = (Invocable) engine;

        }catch (Exception e){
            Log.error("Error initializing script!",e);
        }

    }

    public static void main(String[] args) {
        ScriptCheck check = new ScriptCheck("context.tryCount == 2");

        ConversationState state = new ConversationState("Test",new Conversation(null)) {
            @Override
            protected void init() {

            }
        };

        state.getContext().set("tryCount",2);

        check.evaluate(state);


    }
}

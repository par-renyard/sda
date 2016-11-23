package com.staples.chatbot.poc;

import com.staples.chatbot.ConversationContext;
import com.staples.chatbot.IntentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by benquintana on 11/17/16.
 *
 * A conversation state is a certain state in a conversation
 * It has a top level objective.  For example:  extracting the order number from dialogue
 * or maybe even figuring out what the intent of the contact is in the first place.
 *
 * A Conversation State has a series of rules that are evaluated.
 * Rules also have Actions which can be performed.
 *
 */
public class ConversationState {

    private static final Logger Log = LoggerFactory.getLogger(ConversationState.class);

    protected IntentResponse currentIntent;
    protected String currentUtterance;

    private static final String UTTERANCE_CT="utterance_ct";
    private static final String UNDERSTOOD_UTTERANCE_CT="understood_utterance_ct";
    private static final String ENTRY_CT="entry_ct";


    private ConsoleClient channel;
    private Conversation conversation;

    private ConversationContext context = new ConversationContext();

//    protected List<Rule> intentRules = new ArrayList<>();
//    protected List<Rule> entryRules = new ArrayList<>();
//
//
//    protected Action defaultAction;

    public ConsoleClient getChannel() {
        return channel;
    }

    private String name;

    private ConversationRules rules;

    public ConversationState(String name, Conversation conversation) {

        this.name=name;
        this.conversation=conversation;
        this.channel=conversation.getChannel();

        init();
    }

    public void receiveUtterance(String utterance){
        this.currentUtterance=utterance;
        conversation.getContext().increment(UTTERANCE_CT);
        getContext().increment(UTTERANCE_CT);

        this.currentIntent = conversation.getCognitiveClient().getIntent(utterance);

        boolean matchedIntent = false;

        int i=0;

        while(i<rules.getIntentRules().size()&&!matchedIntent){
            Rule rule = rules.getIntentRules().get(i);
            if(rule.evaluate(this)){
                //
                Log.debug("Matched to Rule [{}]",i+1);
                matchedIntent=true;
                rule.execute(this);
                getContext().increment(UNDERSTOOD_UTTERANCE_CT);
                conversation.getContext().increment(UNDERSTOOD_UTTERANCE_CT);
            }
            i++;

        }
//            ConditionalAction rule = intentRules.get(i);
//            if(rule.evaluate(this)){
//                Log.debug("Matched to Rule [{}]",i+1);
//                matchedIntent=true;
//                rule.execute(this);
//                getContext().increment(UNDERSTOOD_UTTERANCE_CT);
//                conversation.getContext().increment(UNDERSTOOD_UTTERANCE_CT);
//            }else {
//                i++;
//            }
//        }

        if(!matchedIntent){
            Log.debug("Matched 0 intents");
            for(Rule rule:rules.getDefaultRules()){
                rule.execute(this);
            }
//            defaultAction.execute(this);
        }


    }

    public IntentResponse getCurrentIntent() {
        return currentIntent;
    }

    public void setCurrentIntent(IntentResponse currentIntent) {
        this.currentIntent = currentIntent;
    }

    public String getCurrentUtterance() {
        return currentUtterance;
    }

    public void setCurrentUtterance(String currentUtterance) {
        this.currentUtterance = currentUtterance;
    }

    public ConversationContext getContext() {
        return context;
    }


    public int getEntryCount() {
        return context.getInt(ENTRY_CT);
    }

    public void enterContext(String message) {

        int entryCt = this.context.getInt(ENTRY_CT);
        // Wipe the context...
        this.context = new ConversationContext();
        context.set(ENTRY_CT,++entryCt);


        // Just execute all the entry rules...
        for(Rule rule:this.rules.getEntryRules()){
            rule.execute(this);
        }

        if(message!=null){
            this.receiveUtterance(message);
        }

    }

    protected  void init() {
        this.rules=ConversationRulesParser.getRules(this.name);
    }

    public String getName() {
        return name;
    }

    public Conversation getConversation() {
        return conversation;
    }
}

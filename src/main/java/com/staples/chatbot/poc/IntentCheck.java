package com.staples.chatbot.poc;

import com.staples.chatbot.Intent;
import com.staples.chatbot.IntentResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benquintana on 11/19/16.
 *
 */
public class IntentCheck implements Check {

    private List<Intent> intents = new ArrayList<>();
    private List<Double> confidences = new ArrayList<>();


    public IntentCheck(Intent intent,double confidence){
        this.intents.add(intent);
        this.confidences.add(confidence);
    }


    public IntentCheck(Intent[] intentList,double[] confidenceList){

        for(Intent intent:intentList){
            this.intents.add(intent);
        }
        for(Double conf:confidenceList){
            this.confidences.add(conf);
        }
    }

    public IntentCheck(List intentList,List confidenceList){

        this.intents.addAll(intentList);
        this.confidences.addAll(confidenceList);

    }

    /** they all have the same confidence value to check */
    public IntentCheck(List intentList,double confidence){

        this.intents.addAll(intentList);
        for(Intent i:intents){
            this.confidences.add(confidence);
        }

    }


    @Override
    public boolean evaluate(ConversationState state) {
        if(state.getCurrentIntent()==null||state.getCurrentIntent().getIntent()==null){
            return false;
        }
        IntentResponse intent = state.getCurrentIntent();

        for(int i=0;i<intents.size();i++){
            if(intent.getIntent()==intents.get(i)&&intent.getConfidence()>=confidences.get(i)){
                return true;
            }
        }

        return false;

    }

}

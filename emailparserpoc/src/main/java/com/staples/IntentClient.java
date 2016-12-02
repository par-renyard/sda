package com.staples;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.List;

/**
 * Created by benquintana on 11/17/16.
 *
 */
public class IntentClient {

//    private static final Logger Log = LoggerFactory.getLogger(IntentClient.class);


    //    public static final String URL =  "https://gateway.watsonplatform.net/conversation/api";
    public static final String PASSWORD = "7cXUjVQTALp6";
    public static final String USERNAME = "a9759564-668f-46b3-821f-545fa1910dfa";
    public static final String workspaceId = "42669ace-e112-4537-9e53-668775f78a69";


    private boolean DEBUG_OUT = true;

    public static void main(String[] args) {

        IntentClient client = new IntentClient();

        client.init();

        client.getIntent("hey there how are you?");
    }

    private ConversationService service;

    public void init() {
        //  ConversationService.VERSION_DATE_2016_09_20,
        service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11, USERNAME, PASSWORD);
        System.out.println("Conversation service initialized.");
    }


    public IntentResponse getIntent(String inputText) {

        MessageRequest newMessage = new MessageRequest.Builder()
                .inputText(inputText)
                // Replace with the context obtained from the initial request
                //.context(...)
                .build();

        MessageResponse response = service
                .message(workspaceId, newMessage)
                .execute();

//        System.out.println("response:"+response);

        Intent intent = getIntent(response.getIntents());

        IntentResponse myIntent = new IntentResponse();
        myIntent.setConfidence(intent.getConfidence());
        myIntent.setInputText(inputText);
        myIntent.setIntentName(intent.getIntent());

//        Log.debug("input      :  {}",response.getInputText());
//        Log.debug("intent     :  {}",intent.getIntent());
//        Log.debug("confidence :  {}",intent.getConfidence());

        return myIntent;



    }

    private Intent getIntent(List<Intent> intents){
        double maxConfidece = -1;
        Intent bestIntent = null;

        for(Intent intent:intents){
            if(intent.getConfidence()>maxConfidece){
                bestIntent=intent;
                maxConfidece=intent.getConfidence();
            }
        }

        return bestIntent;

    }


}
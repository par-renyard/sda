package com.staples.sda.dialog.message.intent;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.staples.sda.dialog.Intents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@Service
public class IbmWatsonConversationClient implements IntentExtractor {
    private static final Logger Log = LoggerFactory.getLogger(IbmWatsonConversationClient.class);

    //    public static final String URL =  "https://gateway.watsonplatform.net/conversation/api";
    public static final String PASSWORD = "7cXUjVQTALp6";
    public static final String USERNAME = "a9759564-668f-46b3-821f-545fa1910dfa";
    public static final String workspaceId = "42669ace-e112-4537-9e53-668775f78a69";

    private static Map<String, Intents> watsonIntentMap = new HashMap<String, Intents>();
    
    static {
    	watsonIntentMap.put("ShipmentTracking", Intents.WIMS);
    	watsonIntentMap.put("Greeting", Intents.GREETING);
    	watsonIntentMap.put("Affirmative", Intents.YES);
    	watsonIntentMap.put("Negative", Intents.NO);
    	watsonIntentMap.put("AgentTransfer", Intents.AGENT);
    	watsonIntentMap.put("Wait", Intents.WAIT);
    	
    }

    private ConversationService service;

    @PostConstruct
    public void init() {
        //  ConversationService.VERSION_DATE_2016_09_20,
        service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11, USERNAME, PASSWORD);
        Log.info("IBM Watson Conversation service initialized.");
    }


    /* (non-Javadoc)
	 * @see com.staples.sda.dialog.message.intent.IntentExtractor#getIntents(java.lang.String)
	 */
    @Override
	public List<com.staples.sda.dialog.message.intent.Intent> getIntents(String inputText) {

        MessageRequest newMessage = new MessageRequest.Builder()
                .inputText(inputText)
                // Replace with the context obtained from the initial request
                //.context(...)
                .build();

        MessageResponse response = service
                .message(workspaceId, newMessage)
                .execute();


        List<com.staples.sda.dialog.message.intent.Intent> intents = new ArrayList<com.staples.sda.dialog.message.intent.Intent>();
        for (Intent intent:response.getIntents()) {
            Log.debug("input      :  {}",response.getInputText());
            Log.debug("intent     :  {}",intent.getIntent());
            Log.debug("confidence :  {}",intent.getConfidence());
        	
        	com.staples.sda.dialog.message.intent.Intent internalIntent = translateWatsonIntent(intent, inputText);
        	if (internalIntent != null) {
        		intents.add(internalIntent);
        	}
        }
        return intents;
    }
    
    private com.staples.sda.dialog.message.intent.Intent translateWatsonIntent(Intent intent, String inputText) {
    	Intents internalIntent = watsonIntentMap.get(intent.getIntent());
    	if (internalIntent != null) {
    		return new com.staples.sda.dialog.message.intent.Intent(internalIntent, intent.getIntent(), intent.getConfidence(), inputText);
    	} else {
    		Log.info("Unknown intent [{}]", intent.getIntent());
    		return null;
    	}
    }
}

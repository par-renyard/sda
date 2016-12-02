package com.staples.chatbot.poc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.staples.chatbot.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benquintana on 11/20/16.
 *
 */
public class ConversationRulesParser {

    private static Map<String,ConversationRules> rules = new HashMap<>();

    private static final Logger Log = LoggerFactory.getLogger(ConversationRulesParser.class);

    public static synchronized ConversationRules getRules(String stateName){
        ConversationRules r = rules.get(stateName);
        if(r!=null){
            return r;
        }else {
            r = parseRules(stateName);
            rules.put(stateName,r);
            return r;
        }
    }


    /**
     * Assumes naming convention with the state name ...
     *
     *  StateName + Rules + .json
     *
     *  File must be in the classpath.
     *
     * @param stateName
     * @return
     */
    private static ConversationRules parseRules(String stateName) {
        ObjectMapper mapper = new ObjectMapper();

        String fileName = stateName+"Rules.json";

        Log.info("Trying to load rules file [{}]",fileName);

        URL url = Main.class.getClassLoader().getResource(fileName);

        JsonNode json =null;

        try {
            json = mapper.readTree(new File(url.getFile()));
        }catch (Exception e){

        }

        System.out.println(json.get("name"));

        ArrayNode rulesJson = (ArrayNode) json.get("rules");

        ConversationRules rules = new ConversationRules();

        for(JsonNode ruleJson:rulesJson){
            System.out.println(ruleJson);
            String scope = ruleJson.get("scope").asText();
            Rule rule = parseRule(ruleJson);

            System.out.println("scope: "+scope);

            if("intent".equalsIgnoreCase(scope)){
                rules.addIntentRule(rule);
            }else if("default".equalsIgnoreCase(scope)){
                rules.addDefaultRule(rule);
            }else if("entry".equalsIgnoreCase(scope)){
                rules.addEntryRule(rule);
            }

        }

        return rules;


//        JsonNode checks =
    }

    private static Rule parseRule(JsonNode json) {
        Log.debug("Parsing rule from:  {}",json);

        String name = json.get("name").asText();

        if(name.equals("Re-try or send to agent")){
            System.out.println("ok this is it!");
        }
        ArrayNode checks = (ArrayNode) json.get("check");

        ArrayNode trueActions = (ArrayNode) json.get("true");

        ArrayNode falseActions = (ArrayNode) json.get("false");

        String clazz = null;
        if(json.get("class")!=null){
            clazz = json.get("class").asText();
            Log.info("Trying to load custom rule from class [{}]",clazz);

            try {
                Rule r = (Rule)Class.forName(clazz).newInstance();
                r.setName(name);
                return r;
            }catch (Exception e){
                Log.error("Error loading custom rule",e);
                throw new UnsupportedOperationException("Error loading custom rule");
            }
        }

        Rule rule = new Rule();
        rule.setName(name);

        for(JsonNode checkJson:checks){
            rule.addCheck(parseCheck(checkJson));
        }

        if(trueActions!=null){
            for(JsonNode trueJson:trueActions){
                rule.addTrueAction(parseAction(trueJson));
            }
        }

        if(falseActions!=null){
            for(JsonNode node:falseActions){
                rule.addFalseAction(parseAction(node));
            }
        }

        return rule;

    }

    private static Action parseAction(JsonNode json){
        if(json.get("say")!=null){
            String data = json.get("say").asText();
            Action action = new SayAction(data);
            return action;
        }

        if(json.get("transfer")!=null){
            String data = json.get("transfer").asText();

            Action action = new TransferAction(data);
            return action;
        }

        if(json.get("state")!=null){
            String data = json.get("state").asText();
            Action action = new ChangeStateAction(data);
            return action;
        }

        if(json.get("rule")!=null){
            return parseRule(json.get("rule"));
        }

        if(json.get("script")!=null){
            String data = json.get("script").asText();
            Action action = new ScriptAction(data);
            return action;
        }

        if(json.get("class")!=null) {
            String data = json.get("class").asText();

            try {
                return (Action) Class.forName(data).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }


        return null;
    }

    private static Check parseCheck(JsonNode json){
        if(json.get("intent")!=null){
            String intentStr = json.get("intent").asText();
            double confidence = json.get("confidence").asDouble();
            Intent intent = Intent.valueOf(intentStr);
            IntentCheck check = new IntentCheck(intent,confidence);

            return check;
        }

        if(json.get("script")!=null){
            String script = json.get("script").asText();
            ScriptCheck check = new ScriptCheck(script);
            return check;
        }

        if(json.get("intents")!=null){
            ArrayNode intentList = (ArrayNode) json.get("intents");

            ArrayNode confidenceList = null;
            double confidenceForAll = 0;
            if(json.get("confidences")!=null){
                confidenceList = (ArrayNode) json.get("confidences");
            }else {
                confidenceForAll = json.get("confidence").asDouble();
                Log.debug("Confidence for all:  [{}]",confidenceForAll);
            }

            List<Intent> intents = new ArrayList<>();
            List<Double> confidences = new ArrayList<>();

            for(JsonNode intent:intentList){
                intents.add(Intent.valueOf(intent.asText()));
            }

            IntentCheck check = null;

            if(confidenceList!=null){
                for(JsonNode conf:confidenceList){
                    confidences.add(conf.asDouble());
                }
                check = new IntentCheck(intents,confidences);
            }else {
                check = new IntentCheck(intents,confidenceForAll);
            }

            return check;


        }


        return null;
    }

    public static void main(String[] args) {
        ConversationRules rules = getRules("Test");
    }


//    public static ConversationState
}

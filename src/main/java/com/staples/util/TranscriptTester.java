package com.staples.util;

import com.staples.chatbot.IntentClient;
import com.staples.chatbot.IntentResponse;
import com.staples.chatbot.domain.ChatMessage;
import com.staples.chatbot.domain.ChatSession;
import com.staples.chatbot.domain.Party;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benquintana on 11/28/16.
 *
 */
public class TranscriptTester {

    private static Map<Long,ChatSession> sessions = new HashMap<>();

    private static Counter buCt = new Counter();

    private static Counter dayCt = new Counter();


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");

    private static Pattern p = Pattern.compile("(\\d+)\\|\"(.+?)\"\\|(\\d{1})\\|\"(.+?)\"\\|\"(.+?)\"");

    private static List<Long> SKIP_SESSION_IDS=new ArrayList<>();

    private static int loadSkips() {
        FileIterator f = new FileIterator("/Users/benquintana/dev/chatbotpoc/temp/skip.txt");
        String line = "";
        while((line=f.nextLine())!=null){
            try {
                String[] tokens = line.split("\t");
                Long ses = Long.parseLong(tokens[0]);
                SKIP_SESSION_IDS.add(ses);
            }catch (Exception e){
                // ignore
            }
        }

        return SKIP_SESSION_IDS.size();

    }

    private static IntentClient client;

    public static void main(String[] args) {
        System.out.println("Loaded this many sessions to skip:  "+loadSkips());

        FileIterator f = new FileIterator("/Users/benquintana/dev/chatbotpoc/temp/oct_chats.txt");
        String line;
        while((line=f.nextLine())!=null){
            //System.out.println(line);
            store(parse(line));
        }

        System.out.println("This many:  "+sessions.values().size());
        System.out.println(dayCt);
        System.out.println(buCt);

        client = new IntentClient();
        client.init();

//
//        runOne(13632135);
//        runOne(13894295);
//        runOne(13894337);
//        runOne(14156129);

        runAll();

    }

    public static void runAll() {
        int ct=0;

        for(ChatSession session:sessions.values()){
            ++ct;
            session.sort();
            //System.out.println("Session=["+session.getSessionId()+"] first message = ["+session.getMessages().get(0)+"]");
            ChatMessage msg = session.getMessages().get(0);

            printOutputResult(msg);

            if(ct>100){
                System.out.println("STOPPPPPP   ");
                return;
            }

        }

    }

    public static void printOutputResult(ChatMessage msg){
        if(msg.getParty()==Party.Agent){
            System.out.println(getResponse(msg,null));
        }else {
            IntentResponse intent = client.getIntent(msg.getMessageText());
            System.out.println(getResponse(msg,intent));
        }

    }

    public static void runOne(long sessionId){
        ChatSession session = find(sessionId);
        if(session==null){
            System.out.println("Cant find session:  "+session);
            return;
        }

        printOutputResult(session.getMessages().get(0));

    }

    public static ChatSession find(long sessionId){
        return sessions.get(sessionId);
    }
    private static final String getResponse(ChatMessage msg,IntentResponse intent){
        StringBuffer sb = new StringBuffer();
        sb.append(msg.getSessionId()).append("\t");
        sb.append(msg.getMessageDate()).append("\t");
        sb.append(msg.getParty()).append("\t");

        if(intent!=null){
            sb.append(intent.getIntent()).append("\t");
            sb.append(intent.getConfidence()).append("\t");
        }
        else {
            sb.append("none").append("\t");
            sb.append(0).append("\t");
        }
        sb.append(msg.getMessageText()).append("\t");

        return sb.toString();
    }

    private static void store(ChatMessage msg) {

        if(msg==null){
            System.out.println("weird null message...");
            return;
        }

        if(SKIP_SESSION_IDS.indexOf(msg.getSessionId())>=0){
            System.out.println("Skipping:  "+msg.getSessionId());
            return;
        }

        ChatSession session = sessions.get(msg.getSessionId());
        if(session==null){
            session = new ChatSession();
            session.setSessionId(msg.getSessionId());
            sessions.put(session.getSessionId(),session);
            dayCt.increment(day.format(msg.getMessageDate()));
            buCt.increment(msg.getBu());

        }



        session.addMessage(msg);

    }

    private static ChatMessage oldparse(String line){
//        System.out.println(line);

        try {
            String[] tokens = line.split("\\|");
            ChatMessage msg = new ChatMessage();

            msg.setSessionId(Long.parseLong(tokens[0]));

            String dateStr = unquote(tokens[1]);
            Date date = sdf.parse(dateStr);
            msg.setMessageDate(date);

//            System.out.println(date);
//            System.out.println(dateStr);

            msg.setParty(getParty(tokens[2]));
            msg.setMessageText(unquote(tokens[3]));
            msg.setBu(unquote(tokens[4]));

            //System.out.println(msg);
            return msg;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static ChatMessage parse(String line){
//        System.out.println(line);


        Matcher m = p.matcher(line);

        try {


            if (m.find()) {
                String session = m.group(1);
                String dateStr = m.group(2);
                String who = m.group(3);
                String text = m.group(4);
                String bu = m.group(5);

                ChatMessage msg = new ChatMessage();
                msg.setSessionId(Long.parseLong(session));
                msg.setMessageText(text);
                msg.setBu(bu);
                msg.setParty(getParty(who));
                msg.setMessageDate(sdf.parse(dateStr));
//                System.out.println(msg);

                return msg;
            }
        }catch (Exception e){
            System.out.println("ERROR PARSING!");
            e.printStackTrace();
        }

        return null;
    }


    private static Party getParty(String s){
        if("1".equals(s)){
            return Party.Customer;
        }

        if("2".equals(s)){
            return Party.Agent;
        }

        return Party.Unknown;
    }

    private static String unquote(String data){
        return data.replace("\"","");
    }

    private static class Counter {
        private Map<String,Integer> counters = new HashMap<>();

        public int increment(String value){
            Integer cnt = counters.get(value);
            if(cnt==null){
                cnt=new Integer(0);
            }
            ++cnt;
            counters.put(value,cnt);
            return cnt;
        }

        @Override
        public String toString() {
            Iterator<String> i = counters.keySet().iterator();
            StringBuffer sb = new StringBuffer();
            while(i.hasNext()){
                String key = i.next();
                sb.append(key).append("=").append(counters.get(key)).append("\n");
            }

            return "Counter{" +
                    sb.toString()+
                    '}';
        }
    }
}

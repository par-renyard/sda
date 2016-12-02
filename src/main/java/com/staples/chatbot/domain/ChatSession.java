package com.staples.chatbot.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QuiBe002 on 11/28/2016.
 *
 */
public class ChatSession {
    List<ChatMessage> messages = new ArrayList<ChatMessage>();
    private long sessionId;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage msg){
        this.messages.add(msg);
    }

    public void sort() {
        Collections.sort(messages, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage o1, ChatMessage o2) {
                return o1.getMessageDate().compareTo(o2.getMessageDate());
            }
        });
    }
    /**
     * Get messages for specified party
     *
     * @param who
     * @return
     */
    public List getMessages(Party who){
        List<ChatMessage> msgs = new ArrayList<ChatMessage>();
        for(ChatMessage msg:messages){
            if(msg.getParty()!=null&&msg.getParty()==who){
                msgs.add(msg);
            }
        }
        return msgs;
    }

    @Override
    public String toString() {
        return "ChatSession{" +
                "messages=" + messages +
                ", sessionId=" + sessionId +
                '}';
    }
}

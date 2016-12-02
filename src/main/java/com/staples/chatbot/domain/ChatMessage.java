package com.staples.chatbot.domain;

import java.util.Date;

/**
 * Created by QuiBe002 on 11/28/2016.
 */
public class ChatMessage {
    private long sessionId;
    private Date messageDate;
    private Party party;
    private String messageText;
    private String bu;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sessionId=" + sessionId +
                ", messageDate=" + messageDate +
                ", party=" + party +
                ", messageText='" + messageText + '\'' +
                ", bu='" + bu + '\'' +
                '}';
    }

    public String getBu() {
        return bu;
    }
}

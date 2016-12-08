package com.staples.sda.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.statefulj.persistence.annotations.State;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.staples.sda.dialog.message.MessageContext;

public class DialogContext {

	@State
	private String state;
	
	private String conversationId;
	
	// queue for events
	private Queue<String> eventQueue = new ConcurrentLinkedQueue<String>();
	
	private Map<String, Object> variables = new HashMap<String, Object>();
	
	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@JsonIgnore
	public Queue<String> getEventQueue() {
		return eventQueue;
	}

	// state variables
	
	private int malformedCount = 0;
	
	private int remindCount = 0;
	
	public int getRemindCount() {
		return remindCount;
	}

	public void setRemindCount(int remindCount) {
		this.remindCount = remindCount;
	}

	public int getMalformedCount() {
		return malformedCount;
	}

	public void setMalformedCount(int malformedCount) {
		this.malformedCount = malformedCount;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	private MessageContext lastMessage;

	public MessageContext getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(MessageContext lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getState() {
		return state;
	}
	
	@JsonIgnore
	public States getInternalState() {
		return States.valueOf(state);
	}
	
	public Object getVariable(String key) {
		return variables.get(key);
	}
	public void setVariable(String key, Object value) {
		variables.put(key, value);
	}

}

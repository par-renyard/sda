package com.staples.sda.dialog.message;

public abstract class AbstractMessage {
	
	public static enum MessageDirection {
		INBOUND,
		OUTBOUND
	};
	
	private MessageDirection direction;
	private String raw;
	private String conversationId;
	
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public MessageDirection getDirection() {
		return direction;
	}
	public void setDirection(MessageDirection direction) {
		this.direction = direction;
	}
	public String getRaw() {
		return raw;
	}
	public void setRaw(String raw) {
		this.raw = raw;
	}
	public AbstractMessage(MessageDirection direction, String raw, String conversationId) {
		super();
		this.direction = direction;
		this.raw = raw;
		this.conversationId = conversationId;
	}
	
	
	

}

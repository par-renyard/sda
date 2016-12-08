package com.staples.sda.dialog.message;

public class InitMessage extends AbstractMessage {
	
	public InitMessage() {
		super(null, "<init>", null);
	}

	public InitMessage(MessageDirection direction, String conversationId) {
		super(direction, "<init>", conversationId);
	}

}

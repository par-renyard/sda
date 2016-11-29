package com.staples.sda.dialog.message;

public class StandardMessage extends AbstractMessage {
	
	public StandardMessage() {
		super(null, null, null);
	}

	public static class Builder {
		private StandardMessage message = new StandardMessage();
		
		public Builder fromInbound(AbstractMessage inbound) {
			message.setConversationId(inbound != null ? inbound.getConversationId() : null);
			return this;
		}
		public Builder response(String text) {
			message.setDirection(MessageDirection.OUTBOUND);
			return text(text);
		}
		public Builder inbound(String text) {
			message.setDirection(MessageDirection.INBOUND);
			return text(text);
		}
		public Builder text(String text) {
			message.setRaw(text);
			return this;
		}
		public Builder conversationId(String id) {
			message.setConversationId(id);
			return this;
		}
		public StandardMessage build() {
			return message;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}

}

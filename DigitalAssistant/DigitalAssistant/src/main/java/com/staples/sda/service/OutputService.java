package com.staples.sda.service;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.AbstractMessage;
import com.staples.sda.dialog.message.MessageContext;

public interface OutputService {
	
	public void sendStateEntryMessage(DialogContext context, States state);
	
	public void sendStateMessage(DialogContext context, States state, String suffix);
	
	//public void sendUnhandledIntentResponse(DialogContext context, Intents intent);
	
	public void sendIntentResponse(DialogContext context, States state, Intents intent);
	
	public void transferToAgent(MessageContext lastMessage);
	
	public void terminate(MessageContext lastMessage);
	
	public void sendRaw(DialogContext context, String raw);
	
}

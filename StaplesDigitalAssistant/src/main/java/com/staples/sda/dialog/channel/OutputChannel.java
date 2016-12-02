package com.staples.sda.dialog.channel;

import com.staples.sda.dialog.message.AbstractMessage;
import com.staples.sda.dialog.message.MessageContext;

public interface OutputChannel {
	
	public void outputMessage(AbstractMessage message);
	
	public void transferToAgent(MessageContext lastMessage);
	
	public void terminate(MessageContext lastMessage);

}

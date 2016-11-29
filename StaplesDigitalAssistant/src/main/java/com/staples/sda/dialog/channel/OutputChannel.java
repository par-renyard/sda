package com.staples.sda.dialog.channel;

import com.staples.sda.dialog.message.AbstractMessage;

public interface OutputChannel {
	
	public void outputMessage(AbstractMessage message);

}

package com.staples.sda.dialog.channel;

import com.staples.sda.dialog.message.AbstractMessage;

public interface InputChannel {
	public void inputMessage(AbstractMessage message);

}

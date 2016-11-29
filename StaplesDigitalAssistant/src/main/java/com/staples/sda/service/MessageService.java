package com.staples.sda.service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

public interface MessageService {
	
	public void sendDialogMessage(States state, String suffix);
	
	public void sendIntentResponse(Intents intent, String suffix);

}

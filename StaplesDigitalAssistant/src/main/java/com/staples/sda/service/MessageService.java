package com.staples.sda.service;

import org.springframework.statemachine.StateContext;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

public interface MessageService {
	
	public void sendDialogMessage(StateContext<States, Intents> stateContext, States state, String suffix);
	
	public void sendIntentResponse(StateContext<States, Intents> stateContext, Intents intent, String suffix);

}

package com.staples.sda.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.service.MessageService;

@Component
public class StateMachineListenerBean extends StateMachineListenerAdapter<States, Intents> {

	@Autowired
	private MessageService messageService;

	@Override
	public void stateEntered(State<States, Intents> state) {
		messageService.sendDialogMessage(state.getId(), "entry");
	}

	@Override
	public void eventNotAccepted(Message<Intents> event) {
		messageService.sendIntentResponse(event.getPayload(), "response");
	}
	
}

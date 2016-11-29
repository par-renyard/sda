package com.staples.sda.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.service.MessageService;

@Service
public class StateMachineGuardConfig {
	
	@Autowired
	private MessageService messageService;
	
	public Guard<States, Intents> requiresEntityGuard(Entities requiresEntityType) {
		return new Guard<States, Intents>() {

			@Override
			public boolean evaluate(StateContext<States, Intents> context) {
				MessageContext mc = MessageHeaderHelper.getHeader(context, MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT, MessageContext.class);
				if (mc != null && mc.getEntityOfType(requiresEntityType) != null) {
					return true;
				} else {
					State<States, Intents> source = context.getTransition().getSource();
					if (source != null) {
						// attempt to respond with message, if found
						messageService.sendDialogMessage(source.getId(), "guard.not-found");
					}
					return false;
				}
			}
			
		};
	}

}

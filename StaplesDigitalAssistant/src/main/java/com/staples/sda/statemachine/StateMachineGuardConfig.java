package com.staples.sda.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.service.MessageService;
import com.staples.sda.service.SettingsService;

@Service
public class StateMachineGuardConfig {
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ExtendedStateHelper extendedStateHelper;
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	private OutputChannel outputChannel;
	
	public Guard<States, Intents> requiresEntityGuard(Entities requiredEntityType) {
		return new Guard<States, Intents>() {

			@Override
			public boolean evaluate(StateContext<States, Intents> context) {
				MessageContext mc = MessageHeaderHelper.getHeader(context, MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT, MessageContext.class);
				if (mc != null && mc.getEntityOfType(requiredEntityType) != null) {
					return true;
				} else {
					State<States, Intents> source = context.getTransition().getSource();
					if (source != null) {
						// attempt to respond with message, if found
						int malformedCount = extendedStateHelper.accessor().getMalformedCounter();
						int malformedMaxCount = settingsService.getValue("malformed.max-count", source, Integer.class);
						if (malformedCount >= malformedMaxCount - 1) {
							String action = settingsService.getValue("malformed.max-count.action", source, String.class);
							if ("agent".equalsIgnoreCase(action)) {
								messageService.sendIntentResponse(context, Intents.AGENT, "response");
								outputChannel.transferToAgent(mc);
							} else {
								outputChannel.terminate(mc);
							}
						} else {
							messageService.sendDialogMessage(context, source.getId(), "malformed");
						}
						extendedStateHelper.accessor().increaseMalformedCounter();
					}
					return false;
				}
			}
			
		};
	}

}

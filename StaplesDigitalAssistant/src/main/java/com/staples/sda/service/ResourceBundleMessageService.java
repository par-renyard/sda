package com.staples.sda.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.dialog.message.StandardMessage;
import com.staples.sda.statemachine.ExtendedStateAccessor;
import com.staples.sda.statemachine.ExtendedStateHelper;
import com.staples.sda.statemachine.StateMachineWrapper;

@Service
public class ResourceBundleMessageService implements MessageService {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private OutputChannel outputChannel;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void sendDialogMessage(StateContext<States, Intents> stateContext, States state, String suffix) {
		sendMessage(stateContext, "state." + state.toString() + "." + suffix);
	}
	
	@Override
	public void sendIntentResponse(StateContext<States, Intents> stateContext, Intents intent, String suffix) {
		sendMessage(stateContext, "intent." + intent.name() + "." + suffix);
		
	}
	
	private void sendMessage(StateContext<States, Intents> stateContext, String code) {
		MessageContext lastMessage = ExtendedStateAccessor.forState(stateContext.getExtendedState()).getLastMessage();
		if (lastMessage != null) {
			try {
				String message = messageSource.getMessage(code, null, getLocale());
				StandardMessage reponse = StandardMessage.builder().fromInbound(lastMessage.getMessage()).response(message).build();
				outputChannel.outputMessage(reponse);
			} catch (NoSuchMessageException e) {
				//ignore
				log.debug("No message defined for [{}]", code);
			}
		}
	}

	private Locale getLocale() {
		return Locale.getDefault();
	}

}

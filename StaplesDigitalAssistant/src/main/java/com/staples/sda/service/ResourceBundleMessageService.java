package com.staples.sda.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.dialog.message.StandardMessage;
import com.staples.sda.statemachine.ExtendedStateHelper;

@Service
public class ResourceBundleMessageService implements MessageService {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private OutputChannel outputChannel;
	
	@Autowired
	private StateMachine<States, Intents> stateMachine;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void sendDialogMessage(States state, String suffix) {
		sendMessage("state." + state.toString() + "." + suffix);
	}
	
	@Override
	public void sendIntentResponse(Intents intent, String suffix) {
		sendMessage("intent." + intent.name() + "." + suffix);
		
	}
	
	private void sendMessage(String code) {
		if (isReady(stateMachine)) {
			try {
				MessageContext lastMessage = ExtendedStateHelper.getLastMessage(stateMachine.getExtendedState());
				
				String message = messageSource.getMessage(code, null, getLocale());
				StandardMessage reponse = StandardMessage.builder().fromInbound(lastMessage.getMessage()).response(message).build();
				outputChannel.outputMessage(reponse);
			} catch (NoSuchMessageException nsme) {
				log.debug("No such message [{}]", code);
			}
		}		
	}

	private boolean isReady(StateMachine<States, Intents> stateMachine) {
		try {
			stateMachine.getExtendedState();
		} catch (BeanCurrentlyInCreationException e) {
			return false;
		}
		return true;
	}

	private Locale getLocale() {
		return Locale.getDefault();
	}

}

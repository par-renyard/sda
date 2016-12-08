package com.staples.sda.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.dialog.message.StandardMessage;

@Service
public class ResourceBundleOutputService implements OutputService {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private OutputChannel outputChannel;
	
	
	private static final boolean DEBUG = false;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void sendStateEntryMessage(DialogContext context, States state) {
		sendStateMessage(context, state, "entry");
	}
	
	@Override
	public void sendStateMessage(DialogContext context, States state, String suffix) {
		sendMessage(context, "state." + state.toString() + "." + suffix);
		
	}
	/*
	@Override
	public void sendUnhandledIntentResponse(DialogContext context, Intents intent) {
		sendMessage(context, "intent.unhandled." + intent.name() + ".response");
		
	}
	*/
	
	@Override
	public void sendIntentResponse(DialogContext context, States state, Intents intent) {
		if (!sendMessage(context, "intent." + intent.name() + ".state." + state.name() + ".response")) {
			if (!sendMessage(context, "intent." + intent.name() + ".response")) {
				sendMessage(context, "intent.DEFAULT.response");
			}
		}
		
		
	}

	private boolean sendMessage(DialogContext context, String code) {
		MessageContext lastMessage = context.getLastMessage();
		if (lastMessage != null) {
			try {
				String message = messageSource.getMessage(code, null, getLocale());
				if (DEBUG) {
					message = "[" + code + "] " + message;
				}
				sendRaw(context, message);
				return true;
			} catch (NoSuchMessageException e) {
				//ignore
				log.debug("No message defined for [{}]", code);
				if (DEBUG) {
					sendRaw(context, "[" + code + "] ** NO MESSAGE DEFINED **");
				}
			}
		}
		return false;
	}

	private Locale getLocale() {
		return Locale.getDefault();
	}

	@Override
	public void transferToAgent(MessageContext lastMessage) {
		outputChannel.transferToAgent(lastMessage);
		
	}

	@Override
	public void terminate(MessageContext lastMessage) {
		outputChannel.terminate(lastMessage);
		
	}

	@Override
	public void sendRaw(DialogContext context, String raw) {
		StandardMessage reponse = StandardMessage.builder().fromInbound(context.getLastMessage().getMessage()).response(raw).build();
		outputChannel.outputMessage(reponse);
	}





}

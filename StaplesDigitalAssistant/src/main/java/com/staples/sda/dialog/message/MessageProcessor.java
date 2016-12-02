package com.staples.sda.dialog.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.message.AbstractMessage.MessageDirection;
import com.staples.sda.dialog.message.entity.Entity;
import com.staples.sda.dialog.message.entity.EntityAnalyzer;
import com.staples.sda.dialog.message.intent.Intent;
import com.staples.sda.dialog.message.intent.IntentAnalyzer;
import com.staples.sda.statemachine.ExtendedStateHelper;
import com.staples.sda.statemachine.MessageHeaderHelper;
import com.staples.sda.statemachine.StateMachineWrapper;

@Service
public class MessageProcessor {
	
	private Logger log = LoggerFactory.getLogger(MessageProcessor.class);
	
	@Autowired
	private StateMachineWrapper stateMachineWrapper;
	
	@Autowired
	private ExtendedStateHelper extendedStateHelper;
	
	@Autowired
	private IntentAnalyzer intentAnalyzer;
	
	@Autowired
	private EntityAnalyzer entityAnalyzer;
		
	public void handleMessage(AbstractMessage message) {
		List<Intent> intents = intentAnalyzer.analyze(message);
		List<Entity> entities = entityAnalyzer.analyze(message);
		
		if (entities.size() > 0) {
			intents.add(new Intent(Intents.ENTITY_PROVIDED, "entityProvided", 1.0d, ""));
		}			
		MessageContext mc = new MessageContext(message, intents, entities);
		extendedStateHelper.accessor().setLastMessage(mc);
		log.debug("Received message [{}], Thread [{}]", message.getRaw(), Thread.currentThread().getId());
		stateMachineWrapper.getStateMachine().sendEvent(MessageBuilder.withPayload(mc.getHighestConfidenceIntent().getCode()).setHeader(MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT.getHeaderName(), mc).build());
	}
	
	public void initialize(String conversationId) {
		AbstractMessage message = new InitMessage(MessageDirection.INBOUND, conversationId);
		MessageContext mc = new MessageContext(message, new Intent(Intents.INIT, "init", Intent.CONFIDENCE_MAX, ""));
		extendedStateHelper.accessor().setLastMessage(mc);
		stateMachineWrapper.getStateMachine().sendEvent(MessageBuilder.withPayload(mc.getHighestConfidenceIntent().getCode()).setHeader(MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT.getHeaderName(), mc).build());
	}
}

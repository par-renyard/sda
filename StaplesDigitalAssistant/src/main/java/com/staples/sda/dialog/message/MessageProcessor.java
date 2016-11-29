package com.staples.sda.dialog.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.entity.Entity;
import com.staples.sda.dialog.message.entity.EntityAnalyzer;
import com.staples.sda.dialog.message.intent.Intent;
import com.staples.sda.dialog.message.intent.IntentAnalyzer;
import com.staples.sda.statemachine.ExtendedStateHelper;
import com.staples.sda.statemachine.MessageHeaderHelper;


@Service
public class MessageProcessor {
	
	@Autowired
	private StateMachine<States, Intents> stateMachine;
	
	@Autowired
	private IntentAnalyzer intentAnalyzer;
	
	@Autowired
	private EntityAnalyzer entityAnalyzer;
		
	public void handleMessage(AbstractMessage message) {
		List<Intent> intents = intentAnalyzer.analyze(message);
		List<Entity> entities = entityAnalyzer.analyze(message);
		
		if (entities.size() > 0) {
			intents.add(new Intent(Intents.ENTITY_PROVIDED, "entityProvided", 1.0d));
		}			
		MessageContext mc = new MessageContext(message, intents, entities);
		ExtendedStateHelper.setLastMessage(stateMachine.getExtendedState(), mc);
		stateMachine.sendEvent(MessageBuilder.withPayload(mc.getHighestConfidenceIntent().getCode()).setHeader(MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT.getHeaderName(), mc).build());
	}
}

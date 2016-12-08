package com.staples.sda.dialog.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Dialog;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.AbstractMessage.MessageDirection;
import com.staples.sda.dialog.message.entity.Entity;
import com.staples.sda.dialog.message.entity.EntityAnalyzer;
import com.staples.sda.dialog.message.intent.Intent;
import com.staples.sda.dialog.message.intent.IntentAnalyzer;

@Service
public class MessageProcessor {
	
	private Logger log = LoggerFactory.getLogger(MessageProcessor.class);
	
	@Autowired
	private IntentAnalyzer intentAnalyzer;
	
	@Autowired
	private EntityAnalyzer entityAnalyzer;
	
	@Autowired
	private Dialog dialog;
		
	public void handleMessage(AbstractMessage message) throws Exception {
		List<Intent> intents = intentAnalyzer.analyze(message);
		List<Entity> entities = entityAnalyzer.analyze(message);
		
		if (entities.size() > 0) {
			intents.add(new Intent(Intents.ENTITY_PROVIDED, "entityProvided", 1.0d, ""));
		}
		MessageContext mc = new MessageContext(message, intents, entities);
		dialog.onMessage(mc);
	
	}
	
	public void initialize(String conversationId) throws Exception {
		
		AbstractMessage message = new InitMessage(MessageDirection.INBOUND, conversationId);
		MessageContext mc = new MessageContext(message, new Intent(Intents.INIT, "init", Intent.CONFIDENCE_MAX, ""));
		dialog.onMessage(mc);		
	}
}

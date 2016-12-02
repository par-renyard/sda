package com.staples.sda.dialog;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.dialog.message.StandardMessage;
import com.staples.sda.dialog.message.entity.Entity;
import com.staples.sda.dialog.message.intent.Intent;
import com.staples.sda.statemachine.ExtendedStateHelper;
import com.staples.sda.statemachine.MessageHeaderHelper;
import com.staples.sda.statemachine.StateMachineConfig;
import com.staples.sda.test.SpringTestConfiguration;
import com.staples.sda.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SpringTestConfiguration.class, StateMachineConfig.class})
public class DialogTester {
		
	@Autowired
	private StateMachine<States, Intents> stateMachine;
	
	@Autowired
	private ExtendedStateHelper extendedStateHelper;
	
	Logger log = LoggerFactory.getLogger(getClass());

	@Test
	public void testWIMS() {
		assertEquals(States.WAIT_FOR_INTENT, stateMachine.getState().getId());
		
		Message<Intents> msg = createMessage("testId", "testMessage", Intents.GREETING);
		stateMachine.sendEvent(msg);
		
		TestUtils.assertLowestSubState(stateMachine.getState(), States.WAIT_FOR_INTENT);
		
		msg = createMessage("testId", "testMessage", Intents.WIMS);
		stateMachine.sendEvent(msg);
		
		TestUtils.assertLowestSubState(stateMachine.getState(), States.REQUEST_ORDER_NUM);

		msg = createMessage("testId", "testMessage", Intents.ENTITY_PROVIDED);
		stateMachine.sendEvent(msg);
		TestUtils.assertLowestSubState(stateMachine.getState(), States.REQUEST_ORDER_NUM);

		// set entity correctly
		msg = createMessage("testId", "testMessage", Intents.ENTITY_PROVIDED, Entities.ORDER_NUM);
		stateMachine.sendEvent(msg);
		TestUtils.assertLowestSubState(stateMachine.getState(), States.REQUEST_ZIP);

		msg = createMessage("testId", "testMessage", Intents.ENTITY_PROVIDED);
		stateMachine.sendEvent(msg);
		TestUtils.assertLowestSubState(stateMachine.getState(), States.REQUEST_ZIP);
		
		// set entity correctly
		msg = createMessage("testId", "testMessage", Intents.ENTITY_PROVIDED, Entities.ZIP);
		stateMachine.sendEvent(msg);
		TestUtils.assertLowestSubState(stateMachine.getState(), States.ORDER_LOOKUP);
		
		stateMachine.sendEvent(Intents.OP_SUCCESS);
		TestUtils.assertLowestSubState(stateMachine.getState(), States.ORDER_FOUND);
		
	}
	
	private Message<Intents> createMessage(String inboundId, String inboundMessage, Intents intent) {
		StandardMessage inbound = StandardMessage.builder().inbound(inboundMessage).conversationId(inboundId).build();
		
		List<Intent> intents = new ArrayList<Intent>();
		intents.add(new Intent(intent, intent.name(), Intent.CONFIDENCE_MAX, ""));
		MessageContext mc = new MessageContext(inbound, intents, new ArrayList<Entity>());
		extendedStateHelper.accessor().setLastMessage(mc);
		return MessageBuilder.withPayload(mc.getHighestConfidenceIntent().getCode()).setHeader(MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT.getHeaderName(), mc).build();
	}
	
	private Message<Intents> createMessage(String inboundId, String inboundMessage, Intents intent, Entities entity) {
		StandardMessage inbound = StandardMessage.builder().inbound(inboundMessage).conversationId(inboundId).build();
		
		List<Intent> intents = new ArrayList<Intent>();
		intents.add(new Intent(intent, intent.name(), Intent.CONFIDENCE_MAX, ""));
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new Entity("test", entity));
		MessageContext mc = new MessageContext(inbound, intents, entities);
		extendedStateHelper.accessor().setLastMessage(mc);
		return MessageBuilder.withPayload(mc.getHighestConfidenceIntent().getCode()).setHeader(MessageHeaderHelper.MessageHeaders.MESSAGE_CONTEXT.getHeaderName(), mc).build();
	}

}

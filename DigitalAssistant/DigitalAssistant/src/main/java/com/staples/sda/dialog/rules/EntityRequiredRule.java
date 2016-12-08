package com.staples.sda.dialog.rules;

import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.Intents;
import com.staples.sda.service.OutputService;

public class EntityRequiredRule extends RuleBasedTransition {

	private Entities requiredEntity;
	
	private OutputService messageService;
	
	private int malformedMaxCount;
	
	private Intents malformedExceededIntent;
	
	public EntityRequiredRule(State<DialogContext> returnState, State<DialogContext> nextState,
			Action<DialogContext> action, Entities requiredEntity, int malformedMaxCount, Intents malformedExceededIntent, OutputService messageService) {
		super(returnState, nextState, action, messageService);
		this.requiredEntity = requiredEntity;
		this.messageService = messageService;
		this.malformedMaxCount = malformedMaxCount;
		this.malformedExceededIntent = malformedExceededIntent;
	}

	@Override
	protected boolean evaluate(DialogContext context, String event) {
		if (context.getLastMessage().getEntityOfType(requiredEntity) != null) {
			context.setMalformedCount(0);
			context.setVariable(requiredEntity.name(), context.getLastMessage().getEntityOfType(requiredEntity).getValue());
			return true;
		} else {
			if (context.getMalformedCount() >= malformedMaxCount) {
				context.getEventQueue().offer(malformedExceededIntent.name());
			} else {
				//messageService.transferToAgent(context.getLastMessage());
				if (Intents.ENTITY_PROVIDED.name().equals(event)) {
					messageService.sendStateMessage(context, context.getInternalState(), "malformed");	
				} else {
					messageService.sendStateMessage(context, context.getInternalState(), "not-found");
				}
				context.setMalformedCount(context.getMalformedCount() + 1);
			}
			return false;
		}
	}

}

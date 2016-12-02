package com.staples.sda.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnEventNotAccepted;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnTransitionStart;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.TransitionKind;
import org.springframework.stereotype.Component;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.service.MessageService;

@WithStateMachine
@Component
public class StateMachineAnnotationListener {
	
	private Logger log = LoggerFactory.getLogger(StateMachineAnnotationListener.class);
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private OutputChannel outputChannel;

	@OnEventNotAccepted
	public void onEventNotAccepted(StateContext<States, Intents> context) {
		
		log.debug("EventNotAccepted [{}] for StateMachine [{}], Thread [{}, Current state [{}], Substate [{}], isSubmachineState [{}], Composite [{}], Orthoogonal [{}], Simple [{}]", context.getEvent(), context.getStateMachine().getUuid(), Thread.currentThread().getId(), context.getSource().getId(), StateMachineUtils.getLowestSubstate(context.getSource()).getId(), context.getSource().isSubmachineState(), context.getSource().isComposite(), context.getSource().isOrthogonal(), context.getSource().isSimple());
		ExtendedStateAccessor extendedStateAccessor = ExtendedStateAccessor.forState(context.getExtendedState());
		
		int noopCounter = extendedStateAccessor.getNoopCounter();
		messageService.sendIntentResponse(context, context.getMessage().getPayload(), "response");
		if (context.getMessage().getPayload() == Intents.AGENT) {
			outputChannel.transferToAgent(extendedStateAccessor.getLastMessage());
		}
		if (extendedStateAccessor.isAwaitingEntity()) {
			// dialog is waiting for user to input an entity (like order number)
			messageService.sendDialogMessage(context, StateMachineUtils.getLowestSubstate(context.getSource()).getId(), "not-found");
		}		
	}
	
	@OnStateEntry
	public void onStateEntry(StateContext<States, Intents> context) {
		State<States, Intents> state = context.getTarget();
		if (state != null) {
			messageService.sendDialogMessage(context, state.getId(), "entry");
		}
	}
	
	@OnTransitionStart
	public void onTransitionStart(StateContext<States, Intents> context) {
		ExtendedStateAccessor.forContext(context).resetNoopCounter();
	}
}

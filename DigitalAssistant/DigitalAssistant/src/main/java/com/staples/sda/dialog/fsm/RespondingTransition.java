package com.staples.sda.dialog.fsm;

import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.StateActionPair;
import org.statefulj.fsm.model.impl.DeterministicTransitionImpl;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.service.OutputService;

public class RespondingTransition extends DeterministicTransitionImpl<DialogContext> {
	
	private OutputService messageService;

	public RespondingTransition(State<DialogContext> to, Action<DialogContext> action, OutputService messageService) {
		super(to, action);
		this.messageService = messageService;
	}

	public RespondingTransition(State<DialogContext> from, State<DialogContext> to, String event,
			Action<DialogContext> action, OutputService messageService) {
		super(from, to, event, action);
		this.messageService = messageService;
	}

	public RespondingTransition(State<DialogContext> from, State<DialogContext> to, String event, OutputService messageService) {
		super(from, to, event);
		this.messageService = messageService;
	}

	public RespondingTransition(State<DialogContext> to, OutputService messageService) {
		super(to);
		this.messageService = messageService;
	}

	@Override
	public StateActionPair<DialogContext> getStateActionPair(DialogContext stateful, String event, Object... args) {
		StateActionPair<DialogContext> pair = super.getStateActionPair(stateful, event, args);
		// send response to this particular intent, if one exists..
		messageService.sendIntentResponse(stateful, States.valueOf(pair.getState().getName()), Intents.valueOf(event));
		if (stateful.getState() != pair.getState().getName()) {
			// if source state != target state, respond with the appropriate state entry message
			messageService.sendStateEntryMessage(stateful, States.valueOf(pair.getState().getName()));
		} else {
			
		}
		return pair;
	}


}

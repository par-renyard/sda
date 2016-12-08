package com.staples.sda.dialog.rules;

import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.StateActionPair;
import org.statefulj.fsm.model.impl.StateActionPairImpl;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.fsm.RespondingTransition;
import com.staples.sda.service.OutputService;

public abstract class RuleBasedTransition extends RespondingTransition {
	
	private State<DialogContext> returnState;
	private State<DialogContext> nextState;
	private Action<DialogContext> action;
	public RuleBasedTransition(State<DialogContext> returnState, State<DialogContext> nextState,
			Action<DialogContext> action, OutputService outputService) {
		super(nextState, outputService);
		this.returnState = returnState;
		this.nextState = nextState;
		this.action = action;
	}
	@Override
	public StateActionPair<DialogContext> getStateActionPair(DialogContext stateful, String event, Object... args) {
		if (evaluate(stateful, event)) {
			setStateActionPair(new StateActionPairImpl<DialogContext>(nextState, action));
		} else {
			setStateActionPair(new StateActionPairImpl<DialogContext>(returnState, action));
		}
		return super.getStateActionPair(stateful, event, args);
	}
	
	protected abstract boolean evaluate(DialogContext context, String event);
	
}

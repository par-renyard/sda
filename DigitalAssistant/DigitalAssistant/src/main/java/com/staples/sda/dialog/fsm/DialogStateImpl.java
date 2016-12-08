package com.staples.sda.dialog.fsm;

import java.util.Map;

import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.State;
import org.statefulj.fsm.model.Transition;
import org.statefulj.fsm.model.impl.DeterministicTransitionImpl;
import org.statefulj.fsm.model.impl.StateImpl;

import com.staples.sda.dialog.DialogConfig;
import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.rules.RuleBasedTransition;
import com.staples.sda.service.OutputService;

/**
 * Custom implementation of StateImpl that allows rule-based handling of events events.
 * @author PärAnders
 *
 */
public class DialogStateImpl extends StateImpl<DialogContext> {
	
	private Transition<DialogContext> ruleBasedTransition = null;
	private OutputService outputService;
	
	private NoopAction noopAction;
	


	public DialogStateImpl(String name, OutputService outputService) {
		super(name);
		this.outputService = outputService;
		noopAction = new DefaultNoopAction(outputService);
	}

	public NoopAction getNoopAction() {
		return noopAction;
	}

	public void setNoopAction(NoopAction noopAction) {
		this.noopAction = noopAction;
	}
	
	public void addUnhandledTransition(Transition<DialogContext> transition) {
		this.ruleBasedTransition = transition;
	}

	@Override
	public Transition<DialogContext> getTransition(String event) {
		Transition<DialogContext> ret =  super.getTransition(event);

		if (ret == null) {
			ret = ruleBasedTransition;
		}
		if (ret == null && noopAction != null) {
			ret = new DeterministicTransitionImpl<DialogContext>(this, noopAction);
		}
		return ret;
		
	}

	@Override
	public void addTransition(String event, State<DialogContext> next) {
		this.addTransition(event, new RespondingTransition(next, null, outputService));
	}

	@Override
	public void addTransition(String event, State<DialogContext> next, Action<DialogContext> action) {
		this.addTransition(event, new RespondingTransition(next, action, outputService));
	}
	
	@Override
	public void addTransition(String event, Transition<DialogContext> transition) {
		super.addTransition(event, transition);
	}
	
	/**
	 * Add a RuleBasedTransition to this state. This transition is evaluated regardless of which event (Intent) is received
	 * However, if there is a specific transition configured for an event, that transition takes precedence.
	 *
	 * @param rbt
	 */
	public void addRuleBasedTransition(RuleBasedTransition rbt) {
		this.ruleBasedTransition = rbt;
	}

	

}

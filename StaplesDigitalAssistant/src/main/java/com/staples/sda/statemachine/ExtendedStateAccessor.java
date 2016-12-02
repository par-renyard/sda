package com.staples.sda.statemachine;

import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.message.MessageContext;

public class ExtendedStateAccessor {
	
	private ExtendedState extendedState;
	
	public static ExtendedStateAccessor forState(ExtendedState state) {
		return new ExtendedStateAccessor(state);
	}
	public static ExtendedStateAccessor forContext(StateContext<States, Intents> context) {
		return forState(context.getExtendedState());
	}
	
	private ExtendedStateAccessor(ExtendedState state) {
		this.extendedState = state;
	}
	
	public Object getVariable(ExtendedStateVariables variable) {
		return extendedState.get(variable.getVariableName(), variable.getType());
	}
	public void setVariable(ExtendedStateVariables variable, Object value) {
		extendedState.getVariables().put(variable.getVariableName(), value);
	}
	
	public boolean isAwaitingEntity() {
		Object awaiting = getVariable(ExtendedStateVariables.AWAITING_ENITITY);
		return awaiting != null && (Boolean) awaiting;
	}
	public void setAwaitingEntity(boolean yesNo) {
		setVariable(ExtendedStateVariables.AWAITING_ENITITY, Boolean.valueOf(yesNo));
	}
	public void setLastMessage(MessageContext mc) {
		setVariable(ExtendedStateVariables.LAST_MESSAGE, mc);
	}
	public MessageContext getLastMessage() {
		return (MessageContext) getVariable(ExtendedStateVariables.LAST_MESSAGE);
	}
	public void resetNoopCounter() {
		setVariable(ExtendedStateVariables.NOOP_COUNTER, Integer.valueOf(0));
	}
	public void increaseNoopCounter() {
		int val = getNoopCounter();
		setVariable(ExtendedStateVariables.NOOP_COUNTER, Integer.valueOf(val + 1));
	}
	public int getNoopCounter() {
		Integer value = (Integer) getVariable(ExtendedStateVariables.NOOP_COUNTER);
		return (value == null) ? 0 : value.intValue();
	}
	public int getMalformedCounter() {
		Integer value = (Integer) getVariable(ExtendedStateVariables.MALFORMED_COUNTER);
		return (value == null) ? 0 : value.intValue();
	}
	public void increaseMalformedCounter() {
		int val = getMalformedCounter();
		setVariable(ExtendedStateVariables.MALFORMED_COUNTER, Integer.valueOf(val + 1));
	}
	

}

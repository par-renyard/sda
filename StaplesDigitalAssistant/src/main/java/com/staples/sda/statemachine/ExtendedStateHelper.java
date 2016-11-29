package com.staples.sda.statemachine;

import org.springframework.statemachine.ExtendedState;

import com.staples.sda.dialog.message.MessageContext;

public class ExtendedStateHelper {
	
	public static enum Variables {
		
		AWAITING_ENITITY("sda.variable.awaiting_entity", Boolean.class),
		LAST_MESSAGE("sda.variable.last_message", MessageContext.class);
		
		private String variableName;
		private Class<?> type;
		
		private <T> Variables(String variableName, Class<T> type) {
			this.variableName = variableName;
			this.type = type;
		}
		public String getVariableName() {
			return this.variableName;
		}
		public Class<?> getType() {
			return type;
		}
	}
	
	public static Object getVariable(ExtendedState extendedState, Variables variable) {
		return extendedState.get(variable.getVariableName(), variable.getType());
	}
	public static void setVariable(ExtendedState extendedState, Variables variable, Object value) {
		extendedState.getVariables().put(variable.getVariableName(), value);
	}
	
	public static boolean isAwaitingEntity(ExtendedState extendedState) {
		Object awaiting = getVariable(extendedState, Variables.AWAITING_ENITITY);
		return awaiting != null && (Boolean) awaiting;
	}
	public static void setAwaitingEntity(ExtendedState extendedState, boolean yesNo) {
		setVariable(extendedState, Variables.AWAITING_ENITITY, Boolean.valueOf(yesNo));
	}
	public static void setLastMessage(ExtendedState extendedState, MessageContext mc) {
		setVariable(extendedState, Variables.LAST_MESSAGE, mc);
	}
	public static MessageContext getLastMessage(ExtendedState extendedState) {
		return (MessageContext) getVariable(extendedState, Variables.LAST_MESSAGE);
	}

}

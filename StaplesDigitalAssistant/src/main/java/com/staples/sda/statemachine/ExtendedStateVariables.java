package com.staples.sda.statemachine;

import com.staples.sda.dialog.message.MessageContext;

public enum ExtendedStateVariables {
	
	AWAITING_ENITITY("sda.variable.awaiting_entity", Boolean.class),
	LAST_MESSAGE("sda.variable.last_message", MessageContext.class),
	NOOP_COUNTER("sda.variable.noop_counter", Integer.class),
	MALFORMED_COUNTER("sda.variable.malformed_counter", Integer.class),
	REMIND_COUNTER("sda.variable.remind_counter", Integer.class);
	
	private String variableName;
	private Class<?> type;
	
	private <T> ExtendedStateVariables(String variableName, Class<T> type) {
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
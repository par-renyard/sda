package com.staples.sda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

@Service
public class SettingsService {
	
	@Autowired
	private Environment env;
	
	public <T> T getValue(String value, State<States, Intents> state, Class<T> type) {
		T obj = env.getProperty(value + "." + state.getId().name(), type);
		if (obj == null) {
			obj = env.getProperty(value + ".default", type);
		}
		return obj;
	}
	
	/**
	 * Shorthand method to determine if a configured action at the supplied state is to route to agent.
	 * 
	 * Checks if the setting value is equal to "agent"
	 *
	 * @param value The action (e.g. not-found.max-count.action)
	 * @param state Current state
	 * @return true if the value at this state is equal to "agent"
	 */
	public boolean isAgentAction(String value, State<States, Intents> state) {
		return "agent".equalsIgnoreCase(getValue(value, state, String.class));
	}
}

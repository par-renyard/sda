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

}

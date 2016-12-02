package com.staples.sda.statemachine;

import org.springframework.statemachine.state.AbstractState;
import org.springframework.statemachine.state.State;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

public class StateMachineUtils {
	public static State<States, Intents> getLowestSubstate(State<States, Intents> state) {
		State<States, Intents> _state = state;
		while (_state != null && _state.isSubmachineState()) {
			_state = ((AbstractState<States, Intents>) _state).getSubmachine().getState();
		}
		return _state;
	}
	
}

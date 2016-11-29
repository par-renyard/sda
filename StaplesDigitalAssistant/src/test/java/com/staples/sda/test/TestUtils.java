package com.staples.sda.test;

import static org.junit.Assert.*;

import org.springframework.statemachine.state.AbstractState;
import org.springframework.statemachine.state.State;

public class TestUtils {
	
	public static <S, E> State<S, E> getLowestSubstate(State<S, E> state) {
		State<S, E> _state = state;
		while (_state != null && _state.isSubmachineState()) {
			_state = ((AbstractState<S, E>) _state).getSubmachine().getState();
		}
		return _state;
	}
	
	public static <S, E> void assertLowestSubState(State<S, E> state, S expectedState) {
		assertEquals(expectedState, getLowestSubstate(state).getId());
	}

}

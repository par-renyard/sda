package com.staples.sda.statemachine;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.ExtendedState;
import org.springframework.stereotype.Service;

@Service
public class ExtendedStateHelper {
	
	@Autowired
	private StateMachineWrapper stateMachineWrapper;
	
	public ExtendedStateAccessor accessor() {
		if (stateMachineWrapper.isReady()) {
			return ExtendedStateAccessor.forState(stateMachineWrapper.getExtendedState());
		} else {
			return ExtendedStateAccessor.forState(new DummyExtendedState());
		}
	}
	
	private static class DummyExtendedState implements ExtendedState {

		@Override
		public Map<Object, Object> getVariables() {
			return new HashMap<Object, Object>();
		}

		@Override
		public <T> T get(Object key, Class<T> type) {
			return null;
		}

		@Override
		public void setExtendedStateChangeListener(ExtendedStateChangeListener listener) {
		}
		
	}
	


}

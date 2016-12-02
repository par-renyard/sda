package com.staples.sda.statemachine;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

@Component
public class StateMachineWrapper {
	
	private Logger log = LoggerFactory.getLogger(StateMachineWrapper.class);
	
	private StateMachine<States, Intents> stateMachine;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	public void init() {
		log.debug("Creating StateMachineWrapper");
	}
	
	
	@SuppressWarnings("unchecked")
	public StateMachine<States, Intents> getStateMachine() {
		if (stateMachine == null) {	
			stateMachine = applicationContext.getBean(StateMachine.class);
		}
		return stateMachine;
	}
	
	public ExtendedState getExtendedState() {
		return getStateMachine().getExtendedState();
	}
	
	public State<States, Intents> getLowestSubstate() {
		return StateMachineUtils.getLowestSubstate(getStateMachine().getState());
	}
	public boolean isReady() {
		try {
			if (getStateMachine() == null) {
				return false;
			}
			getStateMachine().getExtendedState();
		} catch (BeanCurrentlyInCreationException e) {
			return false;
		}
		return true;
	}

}

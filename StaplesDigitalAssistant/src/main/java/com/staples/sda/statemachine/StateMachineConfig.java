package com.staples.sda.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Intents> {
	
	private Logger log = LoggerFactory.getLogger(StateMachineConfig.class);
	
	@Autowired
	private StateMachineGuardConfig stateMachineGuardConfig;
	
	@Autowired
	private StateMachineActions stateMachineTimerActions;
	
	
	@Override
	public void configure(StateMachineConfigurationConfigurer<States, Intents> config) throws Exception {
		config
			.withConfiguration()
				.autoStartup(true);
	}

	@Override
	public void configure(StateMachineStateConfigurer<States, Intents> states) throws Exception {
		log.debug("Configuring states");
        states
        .withStates()
            .initial(States.WAIT_FOR_INTENT)
            .state(States.WIMS)
            .state(States.WAIT_FOR_INTENT)
            .and()
            .withStates()
            	.parent(States.WIMS)
            	.initial(States.REQUEST_ORDER_NUM)
            	.state(States.REQUEST_ORDER_NUM)
            	.state(States.REQUEST_ZIP)
            	.state(States.ORDER_LOOKUP)
            	.state(States.ORDER_FOUND);
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<States, Intents> transitions) throws Exception {
		log.debug("Configuring transitionss");
        transitions
        .withExternal()
            .source(States.WAIT_FOR_INTENT).event(Intents.WIMS).target(States.WIMS)
            .and()
        .withInternal()
        	.source(States.WIMS)
        	.action(stateMachineTimerActions.remindTimerAction())
        	.timer(60000)
        	.and()
        .withExternal()
            .source(States.REQUEST_ORDER_NUM).event(Intents.ENTITY_PROVIDED).target(States.REQUEST_ZIP)
            .guard(stateMachineGuardConfig.requiresEntityGuard(Entities.ORDER_NUM))
            .and()
        .withExternal()
        	.source(States.REQUEST_ZIP).event(Intents.ENTITY_PROVIDED).target(States.ORDER_LOOKUP)
        	.guard(stateMachineGuardConfig.requiresEntityGuard(Entities.ZIP))
        	.and()
        .withExternal()
	    	.source(States.ORDER_LOOKUP).event(Intents.OP_NOT_FOUND).target(States.REQUEST_ORDER_NUM)
	    	.and()
	    .withExternal()
	    	.source(States.ORDER_LOOKUP).event(Intents.OP_SUCCESS).target(States.ORDER_FOUND);
        
	}
	
}

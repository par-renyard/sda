package com.staples.sda.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.model.State;
import org.statefulj.persistence.memory.MemoryPersisterImpl;

import com.staples.sda.dialog.actions.AgentAction;
import com.staples.sda.dialog.actions.OrderLookupAction;
import com.staples.sda.dialog.actions.RenderOrderAction;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.fsm.DialogStateImpl;
import com.staples.sda.dialog.rules.EntityRequiredRule;
import com.staples.sda.service.OrderLookupService;
import com.staples.sda.service.OutputService;
import com.staples.sda.service.SettingsService;

@Service
public class DialogConfig {
	
	private FSM<DialogContext> fsm = null;
	
	private Map<String, DialogStateImpl> stateMap = new HashMap<String, DialogStateImpl>();
	
	@Autowired
	private OutputService outputService;
	
	@Autowired
	private OutputChannel outputChannel;
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	private OrderLookupService orderLookupService;
	
	@PostConstruct
	public void init() {
		setUpStates();
		setUpTransitions();
		setUpGlobalTransitions();
	}
	
	// STATES
	public void setUpStates() {
		for (States state : EnumSet.allOf(States.class)) {
			stateMap.put(state.name(), new DialogStateImpl(state.name(), outputService));
		}
	}
	
	// GLOBAL TRANSITIONS - valid from any state
	public void setUpGlobalTransitions() {
		for (State<DialogContext> state : stateMap.values()) {
			state.addTransition(Intents.AGENT.name(), state(States.AGENT), new AgentAction(outputService, outputChannel));
		}
	}
	
	// TRANSITIONS	
	public void setUpTransitions() {
		state(States.INIT).addTransition(Intents.INIT.name(), state(States.WAIT_FOR_INTENT));
		
		state(States.WAIT_FOR_INTENT).addTransition(Intents.WIMS.name(), state(States.REQUEST_ORDER_NUM));
		
		state(States.REQUEST_ORDER_NUM).addRuleBasedTransition(
				new EntityRequiredRule(
						state(States.REQUEST_ORDER_NUM), 
						state(States.REQUEST_ZIP), 
						null, 
						Entities.ORDER_NUM, 
						settingsService.getValue("malformed.max-count", States.REQUEST_ORDER_NUM, Integer.class),
						Intents.AGENT,
						outputService));
		
		state(States.REQUEST_ZIP).addRuleBasedTransition(
				new EntityRequiredRule(
						state(States.REQUEST_ZIP), 
						state(States.ORDER_LOOKUP), 
						new OrderLookupAction(orderLookupService, outputService), 
						Entities.ZIP, 
						settingsService.getValue("malformed.max-count", States.REQUEST_ZIP, Integer.class),
						Intents.AGENT,
						outputService));
		
		state(States.ORDER_LOOKUP).addTransition(Intents.OP_NOT_FOUND.name(), state(States.REQUEST_ORDER_NUM));
		
		state(States.ORDER_LOOKUP).addTransition(Intents.OP_SUCCESS.name(), state(States.ORDER_FOUND), new RenderOrderAction(outputService));
		
		state(States.ORDER_LOOKUP).addTransition(Intents.OP_FAILED.name(), state(States.AGENT));
	}
	
	public DialogStateImpl state(States state) {
		return stateMap.get(state.name());
	}
	

	public FSM<DialogContext> getFSM() {
		if (fsm == null) {
			fsm = new FSM<DialogContext>(new MemoryPersisterImpl<DialogContext>(getAllStates(), state(States.INIT)));
		}
		return fsm;
	}
	
	private Collection<State<DialogContext>> getAllStates() {
		Collection<State<DialogContext>> ret = new ArrayList<State<DialogContext>>();
		ret.addAll(stateMap.values());
		return ret;
	}
	
}

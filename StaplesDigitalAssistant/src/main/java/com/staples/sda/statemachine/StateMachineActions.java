package com.staples.sda.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.service.MessageService;
import com.staples.sda.service.SettingsService;

@Component
public class StateMachineActions {
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private OutputChannel outputChannel;
	
	public Action<States, Intents> remindTimerAction() {
		return new Action<States, Intents>() {

			@Override
			public void execute(StateContext<States, Intents> context) {
				MessageContext mc = ExtendedStateAccessor.forContext(context).getLastMessage();
				if (mc != null) {
					long lastMessage = mc.getCreateTime();
					Integer remindCount = context.getExtendedState().get(ExtendedStateVariables.REMIND_COUNTER.getVariableName(), Integer.class);
					Integer remindLimit = settingsService.getValue("remind.max-count", context.getSource(), Integer.class);
					if (remindCount == null || remindCount.intValue() == 0) {
						remindCount = Integer.valueOf(1);
					}
					if (remindCount >= remindLimit) {
						
					} else {
						Integer remindInterval = settingsService.getValue("remind.interval", StateMachineUtils.getLowestSubstate(context.getSource()), Integer.class);
						if (System.currentTimeMillis() - lastMessage > (remindInterval * remindCount)) {
							messageService.sendDialogMessage(context, StateMachineUtils.getLowestSubstate(context.getSource()).getId(), "remind");
							context.getExtendedState().getVariables().put(ExtendedStateVariables.REMIND_COUNTER.getVariableName(), Integer.valueOf(remindCount + 1));
						}
					}
				}
			}
			
		};
	}
	public Action<States, Intents> agentAction() {
		return new Action<States, Intents>() {

			@Override
			public void execute(StateContext<States, Intents> context) {
				MessageContext mc = ExtendedStateAccessor.forContext(context).getLastMessage();
				if (mc != null) {
					messageService.sendIntentResponse(context, Intents.AGENT, "response");
					outputChannel.transferToAgent(mc);
				}
			}
		};
	}
	public Action<States, Intents> terminateAction() {
		return new Action<States, Intents>() {

			@Override
			public void execute(StateContext<States, Intents> context) {
				MessageContext mc = ExtendedStateAccessor.forContext(context).getLastMessage();
				if (mc != null) {
					messageService.sendIntentResponse(context, Intents.TERMINATE, "response");
					outputChannel.terminate(mc);
				}
			}
		};		
	}
	
	public Action<States, Intents> intentResponseAction(Intents code) {
		return new Action<States, Intents>() {
			
			private Intents intent = code;

			@Override
			public void execute(StateContext<States, Intents> context) {
				messageService.sendIntentResponse(context, intent, "response");
				
			}
		};
	}
	
}
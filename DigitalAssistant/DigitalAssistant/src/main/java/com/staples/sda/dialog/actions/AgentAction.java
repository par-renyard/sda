package com.staples.sda.dialog.actions;

import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;
import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.service.OutputService;

public class AgentAction implements Action<DialogContext> {
	
	private OutputChannel outputChannel;
	
	public AgentAction(OutputService messageService, OutputChannel outputChannel) {
		super();
		this.outputChannel = outputChannel;
	}
	@Override
	public void execute(DialogContext stateful, String event, Object... args) throws RetryException {
		outputChannel.transferToAgent(stateful.getLastMessage());
	}

}

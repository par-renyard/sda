package com.staples.sda.dialog.fsm;

import org.statefulj.fsm.RetryException;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.service.OutputService;

public class DefaultNoopAction implements NoopAction {
	
	private OutputService outputService;

	
	public DefaultNoopAction(OutputService outputService) {
		super();
		this.outputService = outputService;
	}


	@Override
	public void execute(DialogContext stateful, String event, Object... args) throws RetryException {
		outputService.sendIntentResponse(stateful, stateful.getInternalState(), stateful.getLastMessage().getHighestConfidenceIntent().getCode());
	}

}

package com.staples.sda.dialog.actions;

import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.Intents;
import com.staples.sda.service.OrderLookupService;
import com.staples.sda.service.OrderLookupService.Order;
import com.staples.sda.service.OutputService;

public class OrderLookupAction implements Action<DialogContext> {
	
	private OrderLookupService orderLookupService;
	
	private OutputService outputService;
	
	

	public OrderLookupAction(OrderLookupService orderLookupService, OutputService outputService) {
		super();
		this.orderLookupService = orderLookupService;
		this.outputService = outputService;
	}



	@Override
	public void execute(DialogContext stateful, String event, Object... args) {
		try {
			Order order = orderLookupService.lookupOrder(stateful.getVariable(Entities.ORDER_NUM.name()).toString());
				
			if (order == null || !order.getDeliveryZip().equals(stateful.getVariable(Entities.ZIP.name()))) {
				// no order or zip didnt match
				stateful.getEventQueue().offer(Intents.OP_NOT_FOUND.name());
			} else  {
				stateful.setVariable("ORDER", order);
				stateful.getEventQueue().offer(Intents.OP_SUCCESS.name());
			}
		} catch (Exception e) {
			stateful.getEventQueue().offer(Intents.OP_FAILED.name());
		}
	}

}

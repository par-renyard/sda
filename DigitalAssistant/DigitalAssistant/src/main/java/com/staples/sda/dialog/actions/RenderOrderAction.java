package com.staples.sda.dialog.actions;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

import com.staples.sda.dialog.DialogContext;
import com.staples.sda.service.OrderLookupService.Order;
import com.staples.sda.service.OutputService;

public class RenderOrderAction implements Action<DialogContext> {
	
	private OutputService outputService;
	
	private Template velocityTemplate;
	

	public RenderOrderAction(OutputService outputService) {
		super();
		this.outputService = outputService;
		Velocity.init();
		velocityTemplate = Velocity.getTemplate("src/main/resources/renderOrderTemplate.vm");
	}



	@Override
	public void execute(DialogContext stateful, String event, Object... args) throws RetryException {
		Order order = (Order) stateful.getVariable("ORDER");
		VelocityContext ctx = new VelocityContext();
		ctx.put("order", order);
		StringWriter sw = new StringWriter();
		velocityTemplate.merge(ctx, sw);
		outputService.sendRaw(stateful, sw.toString());
	}

}

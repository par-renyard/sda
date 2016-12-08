package com.staples.sda.dialog;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.statefulj.fsm.FSM;
import org.statefulj.fsm.TooBusyException;

import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.service.OutputService;
import com.staples.sda.service.SettingsService;

@Service
public class Dialog {
	
	private Logger log = LoggerFactory.getLogger(Dialog.class);
	
	private FSM<DialogContext> fsm = null;
	
	@Autowired
	private DialogContextStore dialogContextStore;
	
	@Autowired
	private DialogConfig dialogConfig;
	
	@Autowired
	private OutputService messageService;
	
	@Autowired
	private SettingsService settingsService;
	
	private Thread reminderThread;
	
	@PostConstruct
	public void init() {
		log.info("Initializing Dialog...");
		fsm = dialogConfig.getFSM();
		reminderThread = new Thread(new ReminderThread());
		reminderThread.start();
	}
	
	public void onMessage(MessageContext message) throws TooBusyException {
		DialogContext ctx = dialogContextStore.getDialogContext(message.getMessage().getConversationId());
		ctx.setLastMessage(message);
		Intents intent = message.getHighestConfidenceIntent().getCode();
		fsm.onEvent(ctx, intent.name(), new Object[0]);
		
		// check for queued events
		String event = null;
		while ((event = ctx.getEventQueue().poll()) != null) {
			fsm.onEvent(ctx, event, new Object[0]);
		}
		dialogContextStore.storeDialogContext(message.getMessage().getConversationId(), ctx);

	}
	private class ReminderThread implements Runnable {
		
		private long INTERVAL = 60000;

		@Override
		public void run() {
			log.info("Starting background reminder thread...");
			while (true) {
				for (DialogContext ctx : dialogContextStore.getAllDialogContext()) {
					int remindInterval = settingsService.getValue("remind.interval", ctx.getInternalState(), Integer.class);
					int remindCount = ctx.getRemindCount();
					int remindLimit = settingsService.getValue("remind.max-count", ctx.getInternalState(), Integer.class);
					if (System.currentTimeMillis() - ctx.getLastMessage().getCreateTime() > (remindInterval * (remindCount + 1))) {
						// time to remind
						if (remindCount >= remindLimit) {
							// timeout						
							messageService.sendStateMessage(ctx, ctx.getInternalState(), "timeout");
							messageService.terminate(ctx.getLastMessage());
						} else {
							messageService.sendStateMessage(ctx, ctx.getInternalState(), "remind");
							ctx.setRemindCount(ctx.getRemindCount() + 1);
						}						
					}
				
				}
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					
				}
			}
			
		}
		
	}
}

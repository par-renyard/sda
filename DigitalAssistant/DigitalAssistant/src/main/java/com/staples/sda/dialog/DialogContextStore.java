package com.staples.sda.dialog;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DialogContextStore {

	private Map<String, DialogContext> contextMap = new HashMap<String, DialogContext>();
	
	private Logger log = LoggerFactory.getLogger(DialogContextStore.class);
	
	public DialogContext getDialogContext(String key) {
		DialogContext ctx = contextMap.get(key);
		if (ctx == null) {
			ctx = new DialogContext();
			contextMap.put(key, ctx);
		}
		return ctx;
	}
	
	public void storeDialogContext(String key, DialogContext ctx) {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(sw, ctx);
		} catch (IOException e) {
			log.error("Failed to serialize context", e);
		}
		log.debug(sw.toString());
	}
	
	public Collection<DialogContext> getAllDialogContext() {
		return contextMap.values();
	}
	
	public void removeDialogContext(String key) {
		contextMap.remove(key);
	}
}

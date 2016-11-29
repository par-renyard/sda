package com.staples.sda.statemachine;

import org.springframework.statemachine.StateContext;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.States;

public class MessageHeaderHelper {
	public static enum MessageHeaders {
		
		MESSAGE_CONTEXT("messageContext");
		
		private String headerName;
		
		private MessageHeaders(String headerName) {
			this.headerName = headerName;
		}
		
		public String getHeaderName() {
			return headerName;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getHeader(StateContext<States, Intents> context, MessageHeaders header, Class<T> type) {
		return (T) context.getMessageHeader(header.getHeaderName());
	}

}

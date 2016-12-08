package com.staples.sda.dialog;

public enum States {
	INIT,
	WAIT_FOR_INTENT,
	WIMS,
	REQUEST_ORDER_NUM(true),
	REQUEST_ZIP(true),
	ORDER_LOOKUP,
	ORDER_FOUND,
	AGENT,
	END;
	
	private boolean expectEntity = false;
	
	private States() {
		
	}
	private States(boolean expectEntity) {
		this.expectEntity = expectEntity;
	}
	
	public boolean isExpectEntity() {
		return expectEntity;
	}
}

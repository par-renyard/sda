package com.staples.sda.dialog;

public enum States {
	INIT,
	WAIT_FOR_INTENT,
	WIMS,
	REQUEST_ORDER_NUM,
	REQUEST_ZIP,
	ORDER_LOOKUP,
	ORDER_FOUND
}

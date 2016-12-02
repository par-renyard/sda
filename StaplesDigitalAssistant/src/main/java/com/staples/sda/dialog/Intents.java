package com.staples.sda.dialog;

public enum Intents {
	GREETING,
	WIMS,
	REWARDS,
	YES,
	NO,
	DONT_HAVE_ORDER_NO,
	PRODUCT_INQUIRY,
	PASSWORD_RESET,
	WAIT,
	GRATITUDE,
	AGENT,
	OTHER,
	NONE,
	ENTITY_PROVIDED, // special case. Denotes that while there may be no direct "intent" detected, an entity was detected.
	OP_SUCCESS, // internal case. Indicates to the state-machine that an external operation succeeded
	OP_FAILED, // internal case. Indicates to the state-machine that an external operation failed
	OP_NOT_FOUND, // internal case. Indicates that a lookup operation did not find anything based on the information provided
	INIT,
	TERMINATE
}

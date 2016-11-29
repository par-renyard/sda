package com.staples.sda.dialog.message.entity;

import com.staples.sda.dialog.Entities;

public class Entity {
	private String value;
	private Entities type;

	public Entities getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public Entity(String value, Entities type) {
		this.value = value;
		this.type = type;
	}
}

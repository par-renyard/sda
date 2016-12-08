package com.staples.sda.dialog.message.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonCreator
	public Entity(@JsonProperty("value") String value, @JsonProperty("type") Entities type) {
		this.value = value;
		this.type = type;
	}
}

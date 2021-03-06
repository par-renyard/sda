package com.staples.sda.dialog.message;

import java.util.ArrayList;
import java.util.List;

import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.message.entity.Entity;
import com.staples.sda.dialog.message.intent.Intent;

public class MessageContext {
	private AbstractMessage message;
	private List<Intent> intents;
	private List<Entity> entities;
	private long createTime;
	
	public AbstractMessage getMessage() {
		return message;
	}
	public void setMessage(AbstractMessage message) {
		this.message = message;
	}
	public List<Intent> getIntents() {
		return intents;
	}
	public void setIntents(List<Intent> intents) {
		this.intents = intents;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	public long getCreateTime() {
		return this.createTime;
	}
	public MessageContext(AbstractMessage message, List<Intent> intents, List<Entity> entities) {
		super();
		this.message = message;
		this.intents = intents;
		this.entities = entities;
		this.createTime = System.currentTimeMillis();
	}
	
	public MessageContext(AbstractMessage message, Intent intent) {
		super();
		this.message = message;
		this.intents = new ArrayList<Intent>();
		if (intent != null) {
			this.intents.add(intent);
		}
		this.entities = new ArrayList<Entity>();
		this.createTime = System.currentTimeMillis();
	}
	
	public Intent getHighestConfidenceIntent() {
		Intent highest = null;
		for (Intent i : intents) {
			if (highest == null || highest.getConfidence()<i.getConfidence()) {
				highest = i;
			}
		}
		return highest;
	}
	public Entity getEntityOfType(Entities type) {
		if (entities != null) {
			for (Entity e : entities) {
				if (e.getType() == type) {
					return e;
				}
			}
		}
		return null;
	}

}

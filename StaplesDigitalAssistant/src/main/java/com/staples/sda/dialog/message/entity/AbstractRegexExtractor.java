package com.staples.sda.dialog.message.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.staples.sda.dialog.Entities;
import com.staples.sda.dialog.message.AbstractMessage;

public class AbstractRegexExtractor {
	private Pattern p = null;
	private Entities type = null;
	
	protected AbstractRegexExtractor(String regex, Entities type) {
		this.p = Pattern.compile(regex);
		this.type = type;
	}
	
	protected String extract(String msg) {
		Matcher m = p.matcher(msg);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}
	
	public Entity extractEntity(AbstractMessage message) {
		String entity = extract(message.getRaw());
		if (entity != null) {
			return new Entity(entity, this.type);
		} else {
			return null;
		}
	}

}

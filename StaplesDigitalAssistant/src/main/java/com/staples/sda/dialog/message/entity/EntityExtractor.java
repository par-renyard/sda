package com.staples.sda.dialog.message.entity;

import com.staples.sda.dialog.message.AbstractMessage;

public interface EntityExtractor {
	public Entity extractEntity(AbstractMessage message);

}

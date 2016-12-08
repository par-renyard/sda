package com.staples.sda.dialog.message.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.message.AbstractMessage;

@Service
public class EntityAnalyzer {

	@Autowired
	private List<EntityExtractor> entityExtractors;

	@SuppressWarnings("serial")
	public List<Entity> analyze(AbstractMessage message) {
		List<Entity> ret = new ArrayList<Entity>() {
			@Override
			public boolean add(Entity e) {
				if (e != null) {
					return super.add(e);
				} else {
					return true;
				}
			}
			
		};
		for (EntityExtractor e : entityExtractors) {
			ret.add(e.extractEntity(message));
		}
		return ret;
	}
	
}

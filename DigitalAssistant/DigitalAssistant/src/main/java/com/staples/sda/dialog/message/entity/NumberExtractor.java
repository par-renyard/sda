package com.staples.sda.dialog.message.entity;

import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;

@Service
public class NumberExtractor extends AbstractRegexExtractor {

	protected NumberExtractor() {
		super("([\\d]+)", Entities.NUMBER);
	}

}

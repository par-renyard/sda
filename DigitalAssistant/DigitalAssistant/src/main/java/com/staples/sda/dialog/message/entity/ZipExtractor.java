package com.staples.sda.dialog.message.entity;

import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;

@Service
public class ZipExtractor extends AbstractRegexExtractor implements EntityExtractor {
	
	public ZipExtractor() {
		super("([\\d]{5})", Entities.ZIP);
	}

}

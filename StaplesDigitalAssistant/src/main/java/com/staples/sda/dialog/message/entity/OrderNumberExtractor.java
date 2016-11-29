package com.staples.sda.dialog.message.entity;

import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;

@Service
public class OrderNumberExtractor extends AbstractRegexExtractor implements EntityExtractor {

	public OrderNumberExtractor() {
		super("\\s([\\d]{10})\\s", Entities.ORDER_NUM);
	}
}

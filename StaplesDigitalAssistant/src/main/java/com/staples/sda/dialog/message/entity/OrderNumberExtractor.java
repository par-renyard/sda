package com.staples.sda.dialog.message.entity;

import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Entities;

@Service
public class OrderNumberExtractor extends AbstractRegexExtractor implements EntityExtractor {

	public OrderNumberExtractor() {
		super("([\\d]{10})", Entities.ORDER_NUM);
	}
}

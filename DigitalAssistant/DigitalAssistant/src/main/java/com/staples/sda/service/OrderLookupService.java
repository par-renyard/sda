package com.staples.sda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderLookupService {
	
	private Logger log = LoggerFactory.getLogger(OrderLookupService.class);

	public static class Order {
		
		private Order(String zip) {
			this.deliveryZip = zip;
		}
		private String deliveryZip;
		public String getDeliveryZip() {
			return deliveryZip;
		}
		
	}
	public Order lookupOrder(String orderNum) throws Exception {
		log.debug("Looking up order [{}]", orderNum);
		if ("1111111112".equals(orderNum)) {
			throw new Exception("ERROR!!!");
		}
		
		if ("1111111111".equals(orderNum)) {
			return new Order("12345");
		} else {
			return null;
		}
	}
}

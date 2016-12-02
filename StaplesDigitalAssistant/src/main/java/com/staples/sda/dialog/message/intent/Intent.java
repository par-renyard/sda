package com.staples.sda.dialog.message.intent;

import com.staples.sda.dialog.Intents;

public class Intent {
	
	private Intents code;
	private String externalCode;
	private double confidence;
	private String inputText;
	
	public static double CONFIDENCE_MAX = 1.0d;
	public static double CONFIDENCE_MIN = 0.01d;
	public Intents getCode() {
		return code;
	}
	public String getExternalCode() {
		return externalCode;
	}
	public double getConfidence() {
		return confidence;
	}
	public Intent(Intents code, String externalCode, double confidence, String inputText) throws IllegalArgumentException {
		super();
		this.code = code;
		this.externalCode = externalCode;
		if (confidence < CONFIDENCE_MIN || confidence > CONFIDENCE_MAX) {
			throw new IllegalArgumentException("Confidence must be between 0.01 and 1.0. Value " + confidence + " is not allowed.");
		}
		this.confidence = confidence;
		this.inputText = inputText;
	}
	public String getInputText() {
		return inputText;
	}


}

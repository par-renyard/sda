package com.staples.sda.dialog.message.intent;

import java.util.ArrayList;
import java.util.List;

import com.staples.sda.dialog.Intents;

public class DummyIntentExtractor implements IntentExtractor {

	@Override
	public List<Intent> getIntents(String inputText) {
		if (inputText.contains("order") || inputText.contains("shipment")) {
			return asIntentList(Intents.WIMS, inputText);
		}
		return null;
	}
	private List<Intent> asIntentList(Intents code, String text) {
		List<Intent> ret = new ArrayList<Intent>();
		ret.add(new Intent(code, code.name(), Intent.CONFIDENCE_MAX, text));
		return ret;
	}

}

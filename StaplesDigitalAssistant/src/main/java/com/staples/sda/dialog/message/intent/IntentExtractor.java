package com.staples.sda.dialog.message.intent;

import java.util.List;

public interface IntentExtractor {

	List<com.staples.sda.dialog.message.intent.Intent> getIntents(String inputText);

}
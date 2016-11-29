package com.staples.sda.dialog.message.intent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.message.AbstractMessage;

@Service
public class IntentAnalyzer {
	
	private static Intent INTENT_NOOP = new Intent(Intents.NONE, "none", Intent.CONFIDENCE_MIN);
	
	public List<Intent> analyze(AbstractMessage message) {
		List<Intent> ret = new ArrayList<Intent>();
		
		if (message.getRaw().contains("order")) {			
			ret.add(new Intent(Intents.WIMS, "WIMS", 100));
		}
		
		if (ret.size() == 0) {
			ret.add(INTENT_NOOP);
		}
		return ret;
	}
	
}

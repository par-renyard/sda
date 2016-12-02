package com.staples.sda.dialog.message.intent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staples.sda.dialog.Intents;
import com.staples.sda.dialog.message.AbstractMessage;

@Service
public class IntentAnalyzer {
	
	private Logger log = LoggerFactory.getLogger(IntentAnalyzer.class);
	
	@Autowired
	private IntentExtractor intentExtractor;
	
	private static Intent INTENT_NOOP = new Intent(Intents.NONE, "none", Intent.CONFIDENCE_MIN, "");
	
	private static final double CONFIDENCE_THRESHOLD = 0.8d;
	
	public List<Intent> analyze(AbstractMessage message) {
		List<Intent> ret = intentExtractor.getIntents(message.getRaw());
		
		ret.removeIf(new Predicate<Intent>() {

			@Override
			public boolean test(Intent t) {
				if (t.getConfidence() < CONFIDENCE_THRESHOLD) {
					log.debug("Intent [{}] removed, below threshold [{}<{}]", t.getCode(), t.getConfidence(), CONFIDENCE_THRESHOLD);
					return true;
				}
				return false;
			}
		});
		
	
		if (ret.size() == 0) {
			ret.add(INTENT_NOOP);
		}
		return ret;
	}
	
}

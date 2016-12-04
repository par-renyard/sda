package com.staples.chatbot.poc;

import com.staples.chatbot.domain.WrappedTrackingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Created by QuiBe002 on 12/4/2016.
 *
 * All done!
 *
 */
public class TerminateChatAction implements Action {
    private static final Logger Log = LoggerFactory.getLogger(TerminateChatAction.class);

    @Override
    public void execute(ConversationState state) {
        state.getChannel().terminate();
    }

}

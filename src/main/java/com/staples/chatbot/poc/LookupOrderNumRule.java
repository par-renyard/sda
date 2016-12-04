package com.staples.chatbot.poc;

import com.staples.chatbot.domain.WrappedTrackingResponse;
import com.staples.chatbot.provider.IPackageTrackingProvider;
import com.staples.chatbot.provider.ProviderFactory;
import com.staples.chatbot.provider.domain.PackageTrackingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by benquintana on 11/20/16.
 *
 * Call the ETPS Api and validate the order number and zip code...
 */
public class LookupOrderNumRule extends Rule {

    private static final Logger Log = LoggerFactory.getLogger(LookupOrderNumRule.class);

    @Override
    protected void init() {
        addCheck(new Check() {
            @Override
            public boolean evaluate(ConversationState state) {

                String orderNum = state.getConversation().getContext().getString("orderNumber");
                String zip = state.getConversation().getContext().getString("zip");

                if(zip==null||orderNum==null){
                    Log.warn("Weird you should have an order an zip code by now!");
                    return false;
                }

                IPackageTrackingProvider provider = ProviderFactory.getInstance().getProvider(IPackageTrackingProvider.class);

                Long orderNumber = Long.parseLong(orderNum);

                Log.debug("About to call package tracking provider [{}]",orderNumber);

                PackageTrackingResponse response = null;
                try {
                    response = provider.trackPackages(orderNumber);
                }catch (Exception e){
                    Log.error("Error calling package tracking for order [{}]",orderNumber,e);
                    return false;
                }



                if(response==null){
                    Log.warn("Package tracking returned null response for order [{}]", orderNumber);
                    return false;
                }

                WrappedTrackingResponse wrapper = new WrappedTrackingResponse(response);

                if(wrapper.getBoxes().size()==0){
                    Log.warn("Package tracking returned 0 boxes for order [{}]",orderNumber);
                    return false;
                }

                String destinationPostalCd = wrapper.getBoxes().get(0).getDestinationPostalCd();
                if(destinationPostalCd.equals(zip)||destinationPostalCd.indexOf(zip)>-1){
                    // zip looks valid

                    // Store in context!
                    state.getConversation().getContext().set("order",wrapper);

                    return true;
                }

                return false;

            }
        });

        addTrueAction(new Action() {
            @Override
            public void execute(ConversationState state) {
                state.getConversation().changeState("HaveValidOrder");
            }
        });


        addFalseAction(new Action() {
            @Override
            public void execute(ConversationState state) {
                state.getChannel().sendMessage("I could not validate that order number and zip code combination.");
                state.getConversation().changeState("CaptureOrder");
            }
        });


    }
}

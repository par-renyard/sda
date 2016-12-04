package com.staples.chatbot.poc;

import com.staples.chatbot.domain.WrappedTrackingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Created by QuiBe002 on 12/4/2016.
 *
 */
public class DescribeOrderStatusAction implements Action {
    private static final Logger Log = LoggerFactory.getLogger(DescribeOrderStatusAction.class);

    @Override
    public void execute(ConversationState state) {

        WrappedTrackingResponse order = (WrappedTrackingResponse) state.getConversation().getContext().get("order");

        if(order==null){
            state.getConversation().getChannel().sendMessage("Let me get you to an agent.");
            state.getConversation().getChannel().transfer();
            return;
        }

        WrappedTrackingResponse.DeliveryState orderState = order.getState();

        if(orderState== WrappedTrackingResponse.DeliveryState.AllBoxesDeliveredSameCarrierSameDay){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String date = sdf.format(order.getBoxes().get(0).getActualDeliveryDt());

            state.getConversation().getChannel().sendMessage(getMessage(
                "I see that your order was delivered on {} via {}.  For more information, click here: http://www.staples.com",
                date, order.getBoxes().get(0).getCarrierServiceName()));
        }

        if(orderState== WrappedTrackingResponse.DeliveryState.AllBoxesDelivered){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String date = sdf.format(order.getBoxes().get(0).getActualDeliveryDt());

            state.getConversation().getChannel().sendMessage(getMessage(
                "I see that your order should be fully delivered.  The last shipment was delivered on {} by {}.  For more information, click here: http://www.staples.com",
                date, order.getBoxes().get(0).getCarrierServiceName()));
        }

        if(orderState== WrappedTrackingResponse.DeliveryState.AllBoxesOFDSameCarrier){
            state.getConversation().getChannel().sendMessage(getMessage(
                "I see that your order is on the local delivery truck and should be delivered today via {} by 5 PM.  For more information, click here.",
                order.getBoxes().get(0).getCarrierServiceName()));
        }

        if(orderState== WrappedTrackingResponse.DeliveryState.AllBoxesOFD){
            state.getConversation().getChannel().sendMessage(getMessage(
                "The remainder of your order is on the local delivery truck and should be delivered today by 5 PM.  For more information, click here.",
                order.getBoxes().get(0).getCarrierServiceName()));
        }

        if(orderState== WrappedTrackingResponse.DeliveryState.AllBoxesOFDSameCarrierOrDelivered){
            state.getConversation().getChannel().sendMessage(getMessage(
                "The remainder of your order is on the local delivery truck and should be delivered today via {} by 5 PM.  For more information, click here.",
                order.getBoxes().get(0).getCarrierServiceName()));
        }

        state.getConversation().changeState("AnythingElse");

    }

    private static String getMessage(String message, Object... parms){
        String text = message;
        text = text.replaceAll("\\{\\}","%s");
        text = String.format(text,parms);

        return text;
    }
}

package com.staples.chatbot.domain;

import com.staples.chatbot.provider.domain.PackageTrackingBox;
import com.staples.chatbot.provider.domain.PackageTrackingResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by QuiBe002 on 12/4/2016.
 *
 * Class to hold the package tracking information.
 * Contains a bunch of utility methods which make the dialogue checks easier
 *
 */
public class WrappedTrackingResponse {

    PackageTrackingResponse trackingResponse=new PackageTrackingResponse();
    List<PackageTrackingBox> boxes;

    private DeliveryState state = DeliveryState.Other;

    public static enum DeliveryState {
        /** all the boxes are already delivered, they were delivered on the same day by the same carrier */
        AllBoxesDeliveredSameCarrierSameDay,
        /** All the boxes are delivered */
        AllBoxesDelivered,
        /** All the boxes are OFD "now" and are delivered by same carrier */
        AllBoxesOFDSameCarrier,
        /** All the boxes are OFD "now" on same carrier or already delivered */
        AllBoxesOFDSameCarrierOrDelivered,
        /** All the boxes are OFD "now" */
        AllBoxesOFD,
        /** Some other box state... */
        Other;
    }

    private String stateCd=DeliveryState.Other+"";

    public String getStateCd() {
        return stateCd;
    }

    public WrappedTrackingResponse(PackageTrackingResponse response){
        this.trackingResponse=response;
        this.boxes=response.getAllBoxes();
        state=getTrackingState();
        stateCd=state+"";
    }

    public DeliveryState getState() {
        return state;
    }

    /** Get the boxes in the specified status */
    public List<PackageTrackingBox> getBoxes(String status){
        List<PackageTrackingBox> boxList = new ArrayList<>();
        for(PackageTrackingBox box:boxes){
            if(status.equals(box.getCurrentStatusCd())){
                boxList.add(box);
            }
        }

        return boxList;
    }

    private DeliveryState getTrackingState() {
        // any boxes?
        if(boxes==null|| boxes.size()<1){
            return DeliveryState.Other;
        }

        List<PackageTrackingBox> deliveredBoxes = getBoxes("DLV");
        if(deliveredBoxes.size()==boxes.size()){
            // All the boxes are delivered

            // See if all the boxes have the same DLV date
            Date date = deliveredBoxes.get(0).getActualDeliveryDt();
            if(allSameDate(date,"DLV",deliveredBoxes) && allBoxesSameCarrier(deliveredBoxes)){
                return DeliveryState.AllBoxesDeliveredSameCarrierSameDay;
            }else {
                return DeliveryState.AllBoxesDelivered;
            }

        }

        List<PackageTrackingBox> ofdBoxes = getBoxes("OFD");

        if(ofdBoxes.size()==boxes.size()&&allSameDate(new Date(),"OFD",ofdBoxes)){
            // All boxes are OFD "now"
            if(allBoxesSameCarrier(ofdBoxes)){
                return DeliveryState.AllBoxesOFDSameCarrier;
            }else {
                return DeliveryState.AllBoxesOFD;
            }
        }

        if(ofdBoxes.size()+deliveredBoxes.size()==boxes.size()&&allSameDate(new Date(),"OFD",ofdBoxes)) {
            // All boxes are OFD or DLV
            if(allBoxesSameCarrier(ofdBoxes)){
                return DeliveryState.AllBoxesOFDSameCarrierOrDelivered;
            }else {
                return DeliveryState.AllBoxesOFD;
            }
        }

        return DeliveryState.Other;

    }


    /** see if all the boxes in this list are on the same carrier */
    public static boolean allBoxesSameCarrier(List<PackageTrackingBox> boxList) {
        if (boxList==null|boxList.size()<1){
            return false;
        }
        String carrier = boxList.get(0).getStandardCarrierCd();
        if(carrier==null){
            // should not happen
            return false;
        }

        for(PackageTrackingBox box:boxList){
            if(!carrier.equals(box.getStandardCarrierCd())){
                return false;
            }
        }
        return true;
    }

    public List<PackageTrackingBox> getBoxes() {
        return boxes;
    }

    /** see if every box in the list has the DLV or OFD date specified */
    private static boolean allSameDate(Date date,String whichDate,List<PackageTrackingBox> boxList){
        if(date==null){
            return false;
        }

        if(boxList==null||boxList.size()<1){
            return false;
        }

        // Convert to string to compare the dates wout time...
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String dateStr = sdf.format(date);

        for(PackageTrackingBox box:boxList){
            Date checkDate=null;
            if("OFD".equals(whichDate)){
                checkDate=box.getLastOFDDt();
            }else if("DLV".equals(whichDate)){
                checkDate=box.getActualDeliveryDt();
            }
            if(checkDate==null){
                return false;
            }

            if(!dateStr.equals(sdf.format(checkDate))){
                return false;
            }
        }

        return true;

    }



}

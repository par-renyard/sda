package com.staples;

/**
 * Created by benquintana on 11/17/16.
 *
 */
public class IntentResponse {

    private double confidence;
    private Intent intent;
    private String inputText;
    private String intentName;

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;

        try {
            setIntent(Intent.valueOf(intentName));
        }catch (Exception e) {
            this.setIntent(null);
        }
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }


    @Override
    public String toString() {
        return "IntentResponse{" +
                "confidence=" + confidence +
                ", intent=" + intent +
                ", inputText='" + inputText + '\'' +
                ", intentName='" + intentName + '\'' +
                '}';
    }
}

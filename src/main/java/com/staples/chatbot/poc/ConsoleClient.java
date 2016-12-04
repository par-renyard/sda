package com.staples.chatbot.poc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by benquintana on 11/17/16.
 *
 */
public class ConsoleClient {

    private boolean loop = true;

    private Conversation conversation = new Conversation(this);

    private void inputLoop() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //client.init();

        boolean started = false;

        while(loop){
            System.out.print("Your chat  :  ");
//            System.out.print("Enter String:  ");
            String s = br.readLine();

            onMessage(s);

        }

    }
    public static void main(String[] args) throws IOException {

        ConsoleClient console = new ConsoleClient();

        console.inputLoop();



    }

    public void sendMessage(String message){
        System.out.println("Staples Bot:  "+message);
    }

    public void onMessage(String message){
        if(message!=null) {

            conversation.receiveUtterance(message);

            if(message!=null&&message.equalsIgnoreCase("exit")) {
                loop=false;
            }
        }

    }

    public void transfer(){
        this.loop=false;
        System.out.println("****** Transferred to Agent *******");
        System.exit(0);
    }

    public void terminate(){
        this.loop=false;
        System.out.println("****** Done with Chat *******");
        System.exit(0);
    }
}

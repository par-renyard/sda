package com.staples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;

/**
 * Created by macbook on 12/1/16.
 */
public class EmailParser {

    public static void main(String[] args) throws Exception {

        EmailParser ep = new EmailParser();

        String host = "outlook.office365.com";
        int port = 993;
        String user = "EasySystem_QA_Staples@Staples.com";
        String password = "$taples1234";


        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect(host, port,user, password);

        Folder inbox = store.getFolder("Inbox");
        System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());
        inbox.open(Folder.READ_ONLY);

     /*  Get the messages which is unread in the Inbox*/
        Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        String content = "";
        for (int i = 0; i < messages.length; i++) {
            System.out.println("Message " + (i + 1));

            System.out.println(ep.getTextFromMessage(messages[i]));

            if(i==10)
                break;


        }
        inbox.close(false);
        store.close();

    }

    public String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }


}

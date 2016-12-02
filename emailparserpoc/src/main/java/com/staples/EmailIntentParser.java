package com.staples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;

import au.com.bytecode.opencsv.CSVWriter;
import com.edlio.emailreplyparser.Email;
import com.edlio.emailreplyparser.EmailParser;
import com.edlio.emailreplyparser.EmailReplyParser;

/**
 * Created by macbook on 12/1/16.
 */
public class EmailIntentParser {

    public static void main(String[] args) throws Exception {

        EmailIntentParser ep = new EmailIntentParser();

        String host = "outlook.office365.com";
        int port = 993;
        String user = "EasySystem_QA_Staples@Staples.com";
        String password = "$taples1234";

        IntentClient client = new IntentClient();
        client.init();

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect(host, port, user, password);

        Folder inbox = store.getFolder("Inbox");
        System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());
        inbox.open(Folder.READ_ONLY);

     /*  Get the messages which is unread in the Inbox*/
        Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        CSVWriter writer = new CSVWriter(new FileWriter("output.csv"), ',');
        String[] entries = "From#Sent time#Subject#Reply text#Intent#Confidence#Tone".split("#");
        writer.writeNext(entries);

        for (int i = 0; i < messages.length; i++) {
            System.out.println("Message " + (i + 1));

            String emailTxt = ep.getTextFromMessage(messages[i]);

            String reply = EmailReplyParser.parseReply(emailTxt);

            reply = reply.replaceAll("[\\t\\n\\r]+"," ");

            if(reply != null && reply.length() > 0){
                IntentResponse response = client.getIntent(reply);

                System.out.println("Input: " + response.getInputText());
                System.out.println("Intent: " + response.getIntent());
                System.out.println("Confidence: " + response.getConfidence());

                String[] input = "FromS-E-P-A-R-A-T-O-RSent timeS-E-P-A-R-A-T-O-RSubject$S-E-P-A-R-A-T-O-RReply textS-E-P-A-R-A-T-O-RIntentS-E-P-A-R-A-T-O-RConfidenceS-E-P-A-R-A-T-O-RTone"
                        .replace("From", messages[i].getFrom()[0].toString())
                        .replace("Sent time", messages[i].getSentDate().toString())
                        .replace("Subject", messages[i].getSubject())
                        .replace("Reply text", reply)
                        .replace("Intent", response.getIntent().toString())
                        .replace("Confidence", response.getConfidence() + "")
                        .split("S-E-P-A-R-A-T-O-R");

                writer.writeNext(input);
            }


            if (i == 10)
                break;

        }
        inbox.close(false);
        store.close();
        writer.close();

    }

    /**
     * Return the primary text content of the message.
     */
    public String getRawText(Part p) throws
            MessagingException, IOException {

        boolean textIsHtml = false;
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getRawText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getRawText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getRawText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getRawText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
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
            MimeMultipart mimeMultipart) throws Exception {
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
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }


}

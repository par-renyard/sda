package com.staples.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by QuiBe002 on 3/30/2016.
 */
public class FileUtil {


    public static void appendFile(String fileName, String data) throws IOException {
        Writer output;
        output = new BufferedWriter(new FileWriter(fileName,true));
        output.append(data+"\n");
        output.close();
    }

    public static void writeFile(String fileName, String data) throws IOException {
        FileOutputStream fop = null;
        File file;

        try {

            file = new File(fileName);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = data.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(String fileName) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return new String(encoded);
    }

}

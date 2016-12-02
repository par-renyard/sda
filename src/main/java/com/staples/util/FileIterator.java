package com.staples.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

/**
 * Created by QuiBe002 on 10/28/2015.
 *
 */
public class FileIterator {

    private static final Logger Log = LoggerFactory.getLogger(FileIterator.class);

    private BufferedReader reader = null;
    boolean initialized=false;

    public FileIterator(String fileName) {
        if(initByPath(fileName)){
            Log.info("Initialized by file path [{}]",fileName);
        }else {
            initByClassPath(fileName);
        }
    }

    private boolean initByPath(String file) {
        Log.info("Trying init by full path [{}]",file);
        try {
            reader = new BufferedReader(new FileReader(file));
            initialized=true;
        } catch (FileNotFoundException e) {
            Log.warn("Could not file file by full path [{}]",file);
            initialized=false;
        }
        return initialized;

    }
    private boolean initByClassPath(String file) {
        try {

            Log.info("Trying init by looking on classpath for file [{}]",file);
            URL url = FileIterator.class.getClassLoader().getResource(file);
            File f = new File(url.toURI());
            reader = new BufferedReader(new FileReader(f));
            Log.info("Successfully init by looking on classpath for file [{}]",file);
            initialized=true;
        }catch (Exception e) {
            Log.error("Could no init by looking on classpath for file [{}]",file,e);
            reader = null;
            initialized=false;
        }

        return initialized;
    }


    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String nextLine() {
        try {
            return reader==null? null:reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isInitialized() {
        return initialized;
    }
}

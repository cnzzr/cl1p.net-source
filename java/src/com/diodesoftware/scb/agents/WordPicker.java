package com.diodesoftware.scb.agents;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Apr 20, 2006
 * Time: 9:49:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordPicker {

    private static WordPicker instance;
    private List dictionary = new ArrayList();
    private Random rnd = new Random(System.currentTimeMillis());
    private Logger log = Logger.getLogger(WordPicker.class);

    public static void init(String dictionaryFile) {
        if (instance == null) {
            instance = new WordPicker(dictionaryFile);
        }
    }

    public static WordPicker getInstnace() {
        return instance;
    }

    private WordPicker(String dictionaryFile) {
        load(dictionaryFile);
    }

    public String pickWord() {
        String word = null;
        synchronized (dictionary) {
            int pos = rnd.nextInt(dictionary.size());
            word = (String) dictionary.get(pos);
        }
        return word;
    }

    private void load(String dictionaryFile) {
        log.info("Loading Dictonary");
        try {

            BufferedReader in = new BufferedReader(new FileReader
                    (dictionaryFile));
            while (in.ready()) {
                dictionary.add(in.readLine());
            }
            in.close();
            log.debug("Dictionary loaded. Size[" + dictionary.size() + "]");
        } catch (Exception e) {
            log.error("Error loading dictionary", e);
        }
    }

}

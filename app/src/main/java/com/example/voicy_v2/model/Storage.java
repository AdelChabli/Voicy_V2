package com.example.voicy_v2.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Storage {

    // TODO A commenter

    public static String PHONE = DirectoryManager.OUTPUT_PHONE;
    public static String SENTENCE = DirectoryManager.OUTPUT_SENTENCE;

    public static void store(JSONArray object, String path) throws JSONException, IOException {
        File exo = new File(path,new Date().toString()+"txt"); // TODO modifier car léo pue

        exo.createNewFile();

        FileOutputStream stream = new FileOutputStream(exo);
        try {
            stream.write(object.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
    public static File[] getExercices(String path) {
        File directory = new File(path);
        File[] list = directory.listFiles();

        return list;
    }
}

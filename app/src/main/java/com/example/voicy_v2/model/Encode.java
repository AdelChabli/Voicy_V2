package com.example.voicy_v2.model;

import android.util.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Encode {

    // TODO A commenter

    public static String getEncode(File file) {
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String encoded = Base64.encodeToString(bytes,0);

        return encoded;
    }
    public static String getEncode(String s) {
        byte[] bytes = s.getBytes();

        String encoded = Base64.encodeToString(bytes,0);

        return encoded;
    }
}

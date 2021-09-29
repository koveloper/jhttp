/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core;

import com.cedarsoftware.util.io.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kgn
 */
public class JsonUtils {
    
    public static com.google.gson.JsonArray toJsonArray(int[] arr) {
        com.google.gson.JsonArray json = new com.google.gson.JsonArray();
        for(int i : arr) {
            json.add(i);
        }
        return json;
    }
    
    public static com.google.gson.JsonArray toJsonArray(String[] arr) {
        com.google.gson.JsonArray json = new com.google.gson.JsonArray();
        for(String i : arr) {
            json.add(i);
        }
        return json;
    }
    
    public static byte[] toByteArray(com.google.gson.JsonArray arr) {
        byte[] bytes = new byte[arr.size()];
        for(int i = 0; i < arr.size(); i++) {
            bytes[i] = (byte) arr.get(i).getAsInt();
        }
        return bytes;
    }
    
    public static int[] toIntArray(com.google.gson.JsonArray arr) {
        int[] bytes = new int[arr.size()];
        for(int i = 0; i < arr.size(); i++) {
            bytes[i] = arr.get(i).getAsInt();
        }
        return bytes;
    }
    
    public static com.google.gson.JsonObject toJsonObject(File folder, String fileName) throws IOException {
        return toJsonObject(new File(folder, fileName));
    }
    
    public static com.google.gson.JsonObject toJsonObject(File file) throws IOException {
        com.google.gson.JsonObject o;
        try (FileReader fr = new FileReader(file)) {
            o = com.google.gson.JsonParser.parseReader(fr).getAsJsonObject();
        }
        return o;
    }
    
    public static com.google.gson.JsonObject toJsonObject(InputStream is) throws IOException {
        com.google.gson.JsonObject o;
        try (InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"))) {
            o = com.google.gson.JsonParser.parseReader(isr).getAsJsonObject();
        }
        return o;
    }
    
    public static String toPrettyOutputString(com.google.gson.JsonElement json) {
        return JsonWriter.formatJson(json.toString(), null, new HashMap<String, Object>() {
            {
                put("PRETTY_PRINT", true);
                put("TYPE", false);
            }
        });
    }
    
    public static String toJSONString(String[] titles, Object[] values) {
        com.google.gson.JsonObject json = new com.google.gson.JsonObject();
        for(int i = 0; i < titles.length; i++) {
            if(values[i] instanceof String) {
                json.addProperty(titles[i], (String) values[i]);
            } else if(values[i] instanceof Integer) {
                json.addProperty(titles[i], (Integer) values[i]);
            } else if(values[i] instanceof Boolean) {
                json.addProperty(titles[i], (Boolean) values[i]);
            }
        }
        return toPrettyOutputString(json);
    }
}

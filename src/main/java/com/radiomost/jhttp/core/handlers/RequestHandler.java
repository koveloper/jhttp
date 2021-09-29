/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core.handlers;

import com.radiomost.jhttp.core.Constants;
import com.radiomost.jhttp.core.InternalCodedServerError;
import com.radiomost.jhttp.core.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kgn
 */
public abstract class RequestHandler {
    
    private final HashMap<String, File> resourcesCache = new HashMap<>();

    public static String getContentType(File f) {
        if(f.isDirectory() || f.getName().endsWith(".json")) {
            return Constants.CONTENT_TYPE_JSON;
        }
        if(f.getName().endsWith(".html")) {
            return Constants.CONTENT_TYPE_TEXT_HTML;
        }
        if(f.getName().endsWith(".js")) {
            return Constants.CONTENT_TYPE_TEXT_JS;
        }
        if(f.getName().endsWith(".css")) {
            return Constants.CONTENT_TYPE_TEXT_CSS;
        }
        if(f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg")) {
            return Constants.CONTENT_TYPE_IMG_JPEG;
        }
        if(f.getName().endsWith(".png")) {
            return Constants.CONTENT_TYPE_IMG_PNG;
        }
        if(f.getName().endsWith(".ico")) {
            return Constants.CONTENT_TYPE_IMG_ICO;
        }
        return Constants.CONTENT_TYPE_OCTET_STREAM;
    }
    
    protected void addToCache(String name, File file) {
        resourcesCache.put(name, file);
    }
    
    protected boolean isInCache(String name) {
        return resourcesCache.containsKey(name);
    }
    
    protected File getFromCache(String name) {
        return resourcesCache.get(name);
    }
    
    protected String getRelativePath(File f, File resourcesFolder) {
        String relativeFilePath = f.getAbsolutePath().replace(resourcesFolder.getAbsolutePath(), "");
        while (relativeFilePath.startsWith("/")) {
            relativeFilePath = relativeFilePath.substring(1, relativeFilePath.length());
        }
        while (relativeFilePath.startsWith("\\")) {
            relativeFilePath = relativeFilePath.substring(1, relativeFilePath.length());
        }
        return relativeFilePath.replace(System.getProperty("file.separator"), "/");
    }
    
    protected File getResource(String name, File resourcesFolder, File searchInFolder) {
        if(name.isEmpty()) {
            return resourcesFolder;
        }
        if (this.isInCache(name)) {
            return this.getFromCache(name);
        }
        if (resourcesFolder == null || !resourcesFolder.exists() || !resourcesFolder.isDirectory()) {
            return null;
        }
        if (searchInFolder == null || !searchInFolder.exists() || !searchInFolder.isDirectory()) {
            return null;
        }
        for (File f : searchInFolder.listFiles()) {
            String relativeFilePath = getRelativePath(f, resourcesFolder);
                
            if (relativeFilePath.equals(name) || (relativeFilePath + "/").equals(name)) {
                this.addToCache(name, f);
                return f;
            }
            if (f.isDirectory()) {
                File r = getResource(name, resourcesFolder, f);
                if (r != null) {
                    this.addToCache(name, r);
                    return r;
                }
            }
        }
        return null;
    }
    
    protected void answer(HttpExchange httpExchange, int responseCode, String body) {
        httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
        byte[] jsonBytes = body.getBytes(Charset.forName("UTF-8"));
        try {
            httpExchange.sendResponseHeaders(responseCode, jsonBytes.length);
            httpExchange.getResponseBody().write(jsonBytes, 0, jsonBytes.length);
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void answer(HttpExchange httpExchange, int responseCode, String prmName, String prmValue) {
        httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
        byte[] jsonBytes = prmName == null || prmValue == null ? new byte[0] : ("{\"" + prmName + "\": \"" + prmValue + "\"}").getBytes(Charset.forName("UTF-8"));
        try {
            httpExchange.sendResponseHeaders(responseCode, jsonBytes.length);
            httpExchange.getResponseBody().write(jsonBytes, 0, jsonBytes.length);
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void answer(HttpExchange httpExchange, int responseCode, String[] prmNames, Object[] prmValues) {
        httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
        String jsonContent = null;
        if(prmNames != null && prmValues != null && prmNames.length > 0 && prmNames.length == prmValues.length) {
            jsonContent = JsonUtils.toJSONString(prmNames, prmValues);
        }
        byte[] jsonBytes = jsonContent == null ? new byte[0] : jsonContent.getBytes(Charset.forName("UTF-8"));
        try {
            httpExchange.sendResponseHeaders(responseCode, jsonBytes.length);
            httpExchange.getResponseBody().write(jsonBytes, 0, jsonBytes.length);            
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void answer(HttpExchange httpExchange, int responseCode, String prmName, int error) {
        httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
        byte[] jsonBytes = ("{\"" + prmName + "\": " + error + "}").getBytes(Charset.forName("UTF-8"));
        try {
            httpExchange.sendResponseHeaders(responseCode, jsonBytes.length);
            httpExchange.getResponseBody().write(jsonBytes, 0, jsonBytes.length);
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void answer(HttpExchange httpExchange, int responseCode, String prmName, boolean error) {
        httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
        byte[] jsonBytes = ("{\"" + prmName + "\": " + error + "}").getBytes(Charset.forName("UTF-8"));
        try {
            httpExchange.sendResponseHeaders(responseCode, jsonBytes.length);
            httpExchange.getResponseBody().write(jsonBytes, 0, jsonBytes.length);
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void answerFileNotFound(HttpExchange httpExchange) {
        this.answer(httpExchange, 404, "error", "resource not found");
    }

    protected void answerServerInternalError(HttpExchange httpExchange, Exception ex) {
        String[] stackTrace = new String[ex.getStackTrace().length];
        int i = 0;
        for(StackTraceElement e : ex.getStackTrace()) {
            stackTrace[i++] = e.toString();
        }
        if(ex instanceof InternalCodedServerError) {
            this.answer(httpExchange, 500, 
                new String[]{
                    "code", 
                    "error", 
                    "trace"
                }, 
                new Object[]{
                    ((InternalCodedServerError) ex).getCode(), 
                    ((InternalCodedServerError) ex).getMessage(),
                    stackTrace
                }
            );
        } else {
            this.answer(httpExchange, 500, 
                new String[]{
                    "error", 
                    "trace"
                }, 
                new Object[]{
                    ex.getMessage(),
                    stackTrace
                }
            );
        }
        
    }

    protected void answerUnsupportedError(HttpExchange httpExchange) {
        this.answer(httpExchange, 406, "error", "unsupported operation");
    }
    
    protected void answerOk(HttpExchange httpExchange) {
        this.answer(httpExchange, 200, "status", "OK");
    }

    protected void answerOkWithoutData(HttpExchange httpExchange) {
        this.answer(httpExchange, 200, (String) null, (String) null);
    }
    
    public abstract boolean isMyURI(String uri);
    
    public abstract boolean handleRequest(HttpExchange httpExchange);
}

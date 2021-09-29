/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core.handlers;

import com.radiomost.jhttp.core.Constants;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author kgn
 */
public class FolderMirrorHandler extends SingleUriHandler {

    private final File folderToMirror;

    public FolderMirrorHandler(String endPoint, File folderToMirror) {
        super(endPoint);
        this.folderToMirror = folderToMirror;
    }


    private void get(String serverPath, File file, HttpExchange httpExchange) throws Exception {
        byte[] content = null;
        if (file == null || !file.exists()) {
            this.answerFileNotFound(httpExchange);
            return;
        }
        if (file.isDirectory()) {
            httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, Constants.CONTENT_TYPE_JSON);
            com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
            obj.addProperty("name", file.getName());
            obj.addProperty("serverPathName", serverPath);
            obj.addProperty("isDirectory", file.isDirectory());
            File[] files = file.listFiles();
            com.google.gson.JsonArray array = new com.google.gson.JsonArray();
            obj.addProperty("innerFilesCount", files == null ? 0 : files.length);
            if (files != null) {
                for (File f : files) {
                    com.google.gson.JsonObject obj_ = new com.google.gson.JsonObject();
                    obj_.addProperty("name", f.getName());
                    obj_.addProperty("isDirectory", f.isDirectory());
                    obj_.addProperty("sizeInBytes", f.length());
                    array.add(obj_);
                }
            }
            obj.add("files", array);
            content = obj.toString().getBytes(Charset.forName("UTF-8"));
        } else {
            httpExchange.getResponseHeaders().add(Constants.CONTENT_TYPE_HEADER_KEY, RequestHandler.getContentType(file));
            try (FileInputStream fis = new FileInputStream(file)) {
                content = IOUtils.toByteArray(fis);
            }
        }
        if (content == null) {
            try {
                httpExchange.sendResponseHeaders(404, 0);
            } catch (IOException ex) {
                Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        try {
            httpExchange.sendResponseHeaders(200, content.length);
            httpExchange.getResponseBody().write(content, 0, content.length);
            httpExchange.getResponseBody().flush();
        } catch (IOException ex) {
            Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public File getAssociatedResource(String uri) {
        String uriToReplace = uri.replaceFirst(this.getEndpoint(), "");
        if ((uriToReplace + "/").equals(this.getEndpoint())) {
            uriToReplace = "";
        }
        if(uriToReplace.isEmpty()) {
            uriToReplace = "index.html";
        }
        return this.getResource(
                uriToReplace,
                this.folderToMirror,
                this.folderToMirror
        );        
    }

    @Override
    public boolean handleRequest(HttpExchange httpExchange) {
        System.out.println("-----------------");
        System.out.println(httpExchange.getRequestMethod() + ": " + httpExchange.getRequestURI().toString());
        File resource = this.getAssociatedResource(httpExchange.getRequestURI().toString());
        
        if (httpExchange.getRequestMethod().equals(Constants.METHOD_GET)) {
            try {
                this.get(
                        httpExchange.getRequestURI().toString(),
                        resource,
                        httpExchange
                );
            } catch (Exception ex) {
                Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
                this.answerServerInternalError(httpExchange, ex);
            }
            return true;
        }
        if (httpExchange.getRequestMethod().equals(Constants.METHOD_OPTIONS)) {
            if (resource == null || !resource.exists()) {
                this.answerFileNotFound(httpExchange);
                return true;
            }
            httpExchange.getResponseHeaders().add(
                    Constants.ACCESS_CONTROL_ALLOW_METHODS_HEADER_KEY,
                    resource.isDirectory() ? 
                            String.join(",", Constants.METHOD_GET, Constants.METHOD_OPTIONS, Constants.METHOD_HEAD, Constants.METHOD_DELETE) 
                            : String.join(",", Constants.ALL_METHODS)
            );
            this.answerOk(httpExchange);
            return true;
        }
        if (httpExchange.getRequestMethod().equals(Constants.METHOD_PUT)) {
            if (resource == null || !resource.exists()) {
                this.answerFileNotFound(httpExchange);
                return true;
            }
            if (resource.isDirectory()) {
                this.answerUnsupportedError(httpExchange);
                return true;
            }
            
            try {
                try (FileOutputStream fos = new FileOutputStream(resource)) {
                    IOUtils.copy(httpExchange.getRequestBody(), fos);
                }
            } catch (Exception ex) {
                Logger.getLogger(FolderMirrorHandler.class.getName()).log(Level.SEVERE, null, ex);
                this.answerServerInternalError(httpExchange, ex);
                return true;
            }
            this.answerOk(httpExchange);
            return true;
        }
        if(httpExchange.getRequestMethod().equals(Constants.METHOD_DELETE)) {
            if (resource == null || !resource.exists()) {
                this.answerFileNotFound(httpExchange);
                return true;
            }
            FileUtils.deleteQuietly(resource);
            this.answerOk(httpExchange);
            return true;
        }
        return false;
    }

    public static String getUri(HttpExchange httpExchange) {
        return httpExchange.getRequestURI().toString();
    }
}

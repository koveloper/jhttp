/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core;

/**
 *
 * @author kgn
 */
public class Constants {
    
    public static final String CORS_HEADER_KEY = "Access-Control-Allow-Origin";
    public static final String CORS_HEADER_VALUE = "*";    
    
    public static final String CORS_HEADER_METHODS_KEY = "Access-Control-Allow-Headers";
    public static final String CORS_HEADER_METHODS_VALUE = "Content-Type";
    
    public static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT_JS = "text/javascript";
    public static final String CONTENT_TYPE_TEXT_CSS = "text/css";
    public static final String CONTENT_TYPE_IMG_JPEG = "image/jpeg";
    public static final String CONTENT_TYPE_IMG_PNG = "image/png";
    public static final String CONTENT_TYPE_IMG_ICO = "image/x-icon";
    
    public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER_KEY = "Access-Control-Allow-Methods";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String[] ALL_METHODS = {METHOD_GET, METHOD_HEAD, METHOD_OPTIONS, METHOD_POST, METHOD_PUT, METHOD_DELETE};
}

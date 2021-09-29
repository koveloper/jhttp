/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core;

import com.radiomost.jhttp.core.handlers.RequestHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kgn
 */
public class HttpServer {

    public static int DEFAULT_PORT = 777;
    public static int THREAD_COUNT = 32;
    private com.sun.net.httpserver.HttpServer serverInstance = null;
    private final RequestHandler[] handlers;

    public HttpServer(RequestHandler[] handlers) {
        this.handlers = Arrays.copyOf(handlers, handlers.length);
    }

    public void init(int port) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);
        serverInstance = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        serverInstance.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                try {
                    if (handlers != null) {
                        httpExchange.getResponseHeaders().add(Constants.CORS_HEADER_KEY, Constants.CORS_HEADER_VALUE);
                        httpExchange.getResponseHeaders().add(Constants.CORS_HEADER_METHODS_KEY, Constants.CORS_HEADER_METHODS_VALUE);
                        for (RequestHandler handler : handlers) {
                            if (handler.isMyURI(httpExchange.getRequestURI().toString()) && handler.handleRequest(httpExchange)) {
                                httpExchange.close();
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        serverInstance.setExecutor(threadPoolExecutor);
        serverInstance.start();
    }
    
    public void init() throws IOException {
        this.init(DEFAULT_PORT);
    }
}

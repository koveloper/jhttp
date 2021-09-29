/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core.handlers;

import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author kgn
 */
public abstract class SimpleUriHandler extends SingleUriHandler {

    public SimpleUriHandler(String endPoint) {
        super(endPoint);
    }

    @Override
    public boolean handleRequest(HttpExchange httpExchange) {
        try {
            Object response = handle(httpExchange.getRequestURI().toString(), httpExchange.getRequestMethod());
            if (response != null) {
                if (response instanceof String) {
                    this.answer(httpExchange, 200, "status", (String) response);
                    return true;
                }
                if (response instanceof Integer) {
                    this.answer(httpExchange, 200, "status", (Integer) response);
                    return true;
                }
                if (response instanceof Boolean) {
                    this.answer(httpExchange, 200, "status", (Boolean) response);
                    return true;
                }
                if (response instanceof Object[][]) {
                    Object[][] map = (Object[][]) response;
                    int size = map.length;
                    String[] names = new String[size];
                    Object[] values = new Object[size];
                    try {
                        for (int i = 0; i < size; i++) {
                            if (map[i].length >= 2) {
                                names[i] = map[i][0].toString();
                                values[i] = map[i][1];
                            } else {
                                this.answerServerInternalError(httpExchange, new Exception("response Object[][] has incorrect row length. Must be >= 2..."));
                                return true;
                            }
                        }
                        this.answer(httpExchange, 200, names, values);
                    } catch (Exception e) {
                        this.answerServerInternalError(httpExchange, e);
                    }
                    return true;
                }
                if (response instanceof String[] && ((String[]) response).length == 2) {
                    this.answer(httpExchange, 200, ((String[]) response)[0], ((String[]) response)[1]);
                    return true;
                }

                this.answerOkWithoutData(httpExchange);
                return true;
            }
        } catch (Exception ex) {
            this.answerServerInternalError(httpExchange, ex);
            ex.printStackTrace();
            return true;
        }
        return false;
    }

    protected abstract Object handle(String uri, String method) throws Exception;
}

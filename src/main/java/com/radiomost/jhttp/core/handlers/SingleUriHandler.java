/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.jhttp.core.handlers;

/**
 *
 * @author kgn
 */
public abstract class SingleUriHandler extends RequestHandler {
    
    private final String endPoint;
    
    public SingleUriHandler(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public final boolean isMyURI(String uri) {
        return uri.startsWith(endPoint) || (uri + "/").startsWith(endPoint);
    }    
    
    public final String getEndpoint() {
        return this.endPoint;
    }
}

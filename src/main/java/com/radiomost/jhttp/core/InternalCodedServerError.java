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
public class InternalCodedServerError extends Exception {
    
    private final int code;

    public InternalCodedServerError(int code, String message) {
        super(message);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return code + ": " + super.getMessage(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getCode() {
        return code;
    }
}

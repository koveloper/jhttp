/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kgn
 */
public class SQLite {

    public static boolean SYS_OUT_ENABLED = false;
    private Connection co = null;
    private String dbFilePathName = null;

    public SQLite(String dbFilePathName) {
        this.dbFilePathName = dbFilePathName;
    }

    public synchronized boolean open() {
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection("jdbc:sqlite:" + dbFilePathName);
            if(SYS_OUT_ENABLED) {
                System.out.println("jdbc:sqlite:" + dbFilePathName + " (OPENED)");
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public synchronized LinkedList<String[]> select(Query.SelectArguments args) {
        if(!args.isCorrect()) {
            return null;
        }
        open();
        try {
            Statement statement = co.createStatement();
            if(SYS_OUT_ENABLED) {
                System.out.println("query is: " + args.getQuery());
            }
            ResultSet rs = statement.executeQuery(args.getQuery());
            LinkedList<String[]> ret = new LinkedList<>();
            while (rs.next()) {
                String[] result = new String[args.getColumnsToOut().size()];
                int i = 0;
                for (String s : args.getColumnsToOut()) {
                    result[i++] = rs.getString(s);
                }
                ret.add(result);
            }
            rs.close();
            statement.close();
            close();
            return ret;
        } catch (SQLException ex) {
            close();
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public synchronized boolean insert(Query.InsertArguments args) {
        if(!args.isCorrect()) {
            return false;
        }
        open();
        if(SYS_OUT_ENABLED) {
            System.out.println("query is: " + args.getQuery());
        }
        Statement statement;
        try {
            statement = co.createStatement();
            statement.executeUpdate(args.getQuery());
            statement.close();
            close();
            return true;
        } catch (SQLException ex) {
            close();
            return false;
        }
    }
    
    public synchronized boolean update(Query.UpdateArguments args) {
        if(!args.isCorrect()) {
            return false;
        }
        open();
        if(SYS_OUT_ENABLED) {
            System.out.println("query is: " + args.getQuery());
        }
        Statement statement;
        try {
            statement = co.createStatement();
            statement.executeUpdate(args.getQuery());
            statement.close();
            close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            close();
            return false;
        }
    }
    
    public synchronized boolean delete(Query.ClearTableArguments args) {
        if(!args.isCorrect()) {
            return false;
        }
        open();
        if(SYS_OUT_ENABLED) {
            System.out.println("query is: " + args.getQuery());
        }
        Statement statement;
        try {
            statement = co.createStatement();
            statement.execute(args.getQuery());
            statement.close();
            close();
            return true;
        } catch (SQLException ex) {
            close();
            return false;
        }
    }
    
    public synchronized boolean delete(Query.DeleteArguments args) {
        if(!args.isCorrect()) {
            return false;
        }
        open();
        if(SYS_OUT_ENABLED) {
            System.out.println("query is: " + args.getQuery());
        }
        Statement statement;
        try {
            statement = co.createStatement();
            statement.execute(args.getQuery());
            statement.close();
            close();
            return true;
        } catch (SQLException ex) {
            close();
            return false;
        }
    }

    public synchronized void close() {
        if (co == null) {
            return;
        }
        try {
            co.close();
            co = null;
            if(SYS_OUT_ENABLED) {
                System.out.println("jdbc:sqlite:" + dbFilePathName + " (CLOSED)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.radiomost.sqlite;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author kgn
 */
public abstract class Query {

    protected String tableName = null;

    private Query(String tableName) {
        this.tableName = tableName;
    }

    public abstract String getQuery();

    public abstract boolean isCorrect();

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    public final Query setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public abstract static class SelectArguments extends Query {

        protected LinkedList<String> columnsToOut = new LinkedList<>();
        protected String conditionColumn = null;
        protected LinkedList<Object> conditonColumnValues = new LinkedList<>();
        protected String order = null;

        private SelectArguments(String tableName) {
            super(tableName);
        }

        public LinkedList<String> getColumnsToOut() {
            return columnsToOut;
        }

        public SelectArguments addColumnToOut(String columnToOut) {
            this.columnsToOut.add(columnToOut);
            return this;
        }

        public SelectArguments setConditionColumn(String conditionColumn) {
            this.conditionColumn = conditionColumn;
            return this;
        }

        public SelectArguments setOrderColumn(String order) {
            this.order = order;
            return this;
        }

        public SelectArguments addConditonColumnValue(Object conditonColumnValue) {
            this.conditonColumnValues.add(conditonColumnValue);
            return this;
        }

        @Override
        public boolean isCorrect() {
            if (tableName == null || tableName.isEmpty() || columnsToOut.isEmpty()) {
                return false;
            }
            return true;
        }
        
        protected String getWhere() {
            return "";
        }
    }

    public static class SelectInArgumentsMultiplexed extends SelectArguments {

        private LinkedList<SelectArguments> conditions = new LinkedList<>();
                
        public SelectInArgumentsMultiplexed(String tableName) {
            super(tableName);
        }
        
        public void addCondition(SelectArguments args) {
            conditions.add(args);
        }

        @Deprecated
        public SelectArguments setConditionColumn(String conditionColumn) {
            return super.setConditionColumn(conditionColumn); //To change body of generated methods, choose Tools | Templates.
        }

        @Deprecated
        public SelectArguments addConditonColumnValue(Object conditonColumnValue) {
            return super.addConditonColumnValue(conditonColumnValue); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getQuery() {
            String query = "SELECT ";
            int size = columnsToOut.size();
            for (String col : columnsToOut) {
                query += col + (size == 1 ? " " : ", ");
                size--;
            }
            query += "FROM " + tableName;
            if(!conditions.isEmpty()) {
                query += " WHERE ";
                size = 0;
                for(SelectArguments args : conditions) {
                    query += args.getWhere() + (size == (conditions.size() - 1) ? "" : " AND ");
                    size++;
                }
            }
            if(order != null) {
                query += " ORDER BY " + order;
            }
            return query + ";";
        }
    }
    
    public static class SelectInArguments extends SelectArguments {

        private boolean _equals = true;
        private boolean _nocase = false;
        
        public SelectInArguments(String tableName) {
            super(tableName);
        }
        
        public SelectInArguments setForNotEquals() {
            _equals = false;
            return this;
        }
        
        public SelectInArguments setNOCASE() {
            _nocase = true;
            return this;
        }

        @Override
        public String getQuery() {
            String query = "SELECT ";
            int size = columnsToOut.size();
            for (String col : columnsToOut) {
                query += col + (size == 1 ? " " : ", ");
                size--;
            }
            query += "FROM " + tableName;
            if (conditionColumn != null && !conditionColumn.isEmpty() && !conditonColumnValues.isEmpty()) {
                query += " WHERE " + getWhere();
            }
            if(order != null) {
                query += " ORDER BY " + order;
            }
            return query + ";";
        }

        @Override
        protected String getWhere() {
            if (conditionColumn != null && !conditionColumn.isEmpty() && !conditonColumnValues.isEmpty()) {
                String query = "";
                if (conditonColumnValues.size() == 1) {
                    Object value = conditonColumnValues.getFirst();
                    query += (_nocase ? "UPPER(" : "") + conditionColumn + (_nocase ? ")" : "") + (_equals ? "=" : "<>") + (_nocase ? "UPPER(" : "") + ((value instanceof Integer || value instanceof Long) ? value : ("'" + value + "'")) + (_nocase ? ")" : "");
                } else {
                    query += (_nocase ? "UPPER(" : "") + conditionColumn + (_nocase ? ")" : "") + " IN (";
                    int size = conditonColumnValues.size();
                    for (Object value : conditonColumnValues) {
                        query += (_nocase ? "UPPER(" : "") + ((value instanceof Integer || value instanceof Long) ? value : ("'" + value + "'")) + (_nocase ? ")" : "") + (size == 1 ? ")" : ",");
                        size--;
                    }
                }
                return query;
            }
            return "";
        }
    }

    public static class SelectLikeArguments extends SelectArguments {

        public SelectLikeArguments(String tableName) {
            super(tableName);
        }

        @Override
        public String getQuery() {
            String query = "SELECT ";
            int size = columnsToOut.size();
            for (String col : columnsToOut) {
                query += col + (size == 1 ? " " : ", ");
                size--;
            }
            query += "FROM " + tableName;
            if (conditionColumn != null && !conditionColumn.isEmpty() && !conditonColumnValues.isEmpty()) {
                query += " WHERE " + getWhere();
            }
            if(order != null) {
                query += " ORDER BY " + order;
            }
            return query + ";";
        }

        @Override
        protected String getWhere() {
            if (conditionColumn != null && !conditionColumn.isEmpty() && !conditonColumnValues.isEmpty()) {
                String query = "";
                if (conditonColumnValues.size() == 1 && conditonColumnValues.getFirst() instanceof String) {
                    query += conditionColumn + " LIKE '" + conditonColumnValues.getFirst() + "'";
                } else {
                    query += conditionColumn + " IN (";
                    int size = conditonColumnValues.size();
                    for (Object value : conditonColumnValues) {
                        query += ((value instanceof Integer || value instanceof Long) ? value : ("'" + value + "'")) + (size == 1 ? ")" : ",");
                        size--;
                    }
                }
                return query;
            }
            return "";
        }
    }

    public static class UpdateArguments extends Query {

        protected LinkedHashMap<String, Object> updateColumns = new LinkedHashMap<>();
        private String conditionColumn = null;
        private Object conditionValue = null;

        public UpdateArguments(String tableName) {
            super(tableName);
        }

        public UpdateArguments addUpdateColumn(String column, Object value) {
            this.updateColumns.put(column, value);
            return this;
        }

        public String getConditionColumn() {
            return conditionColumn;
        }

        public Object getConditionValue() {
            return conditionValue;
        }

        public UpdateArguments setCondition(String column, Object value) {
            this.conditionColumn = column;
            this.conditionValue = value;
            return this;
        }

        @Override
        public boolean isCorrect() {
            if (tableName == null || updateColumns.isEmpty() || conditionColumn == null || conditionColumn.isEmpty() || conditionValue == null) {
                return false;
            }
            return true;
        }

        @Override
        public String getQuery() {
            String query = "UPDATE " + tableName + " SET ";
            int size = updateColumns.size();
            for (String col : updateColumns.keySet()) {
                Object value = updateColumns.get(col);
                if(value == null) {
                    query += col + " = NULL " + (size == 1 ? " " : ", "); 
                } else {
                    query += col + " = " + ((value instanceof Integer || value instanceof Long) ? value : ("'" + value + "'")) + (size == 1 ? " " : ", ");                
                }
                size--;
            }
            if (conditionColumn != null && conditionValue != null) {
                query += "WHERE " + conditionColumn + " = " + ((conditionValue instanceof Integer || conditionValue instanceof Long) ? conditionValue : ("'" + conditionValue + "'"));
            }
            return query + ";";
        }
    }

    public static class UpdateArgumentsMultiplexed extends UpdateArguments {

        private LinkedList<String> conditionColumns = new LinkedList<>();
        private LinkedList<Object> conditionValues = new LinkedList<>();

        public UpdateArgumentsMultiplexed(String tableName) {
            super(tableName);
        }

        @Override
        public String getConditionColumn() {
            return conditionColumns.getFirst();
        }

        @Override
        public Object getConditionValue() {
            return conditionValues.getFirst();
        }
        
        public UpdateArgumentsMultiplexed addCondition(String column, Object value) {
            conditionColumns.add(column);
            conditionValues.add(value);
            return this;
        }

        @Deprecated
        public UpdateArguments setCondition(String column, Object value) {
            return this;
        }

        @Override
        public boolean isCorrect() {
            if (tableName == null || updateColumns.isEmpty() || conditionColumns.isEmpty() || conditionValues.isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        public String getQuery() {
            String query = "UPDATE " + tableName + " SET ";
            int size = updateColumns.size();
            for (String col : updateColumns.keySet()) {
                Object value = updateColumns.get(col);
                if(value == null) {
                    query += col + " = NULL " + (size == 1 ? " " : ", "); 
                } else {
                    query += col + " = " + ((value instanceof Integer || value instanceof Long) ? value : ("'" + value + "'")) + (size == 1 ? " " : ", ");                
                }
                size--;
            }
            if (!conditionColumns.isEmpty() && !conditionValues.isEmpty()) {
                query += "WHERE " + getWhere();
            }
            return query + ";";
        }
        
        private String getWhere() {
            if (!conditionColumns.isEmpty() && !conditionValues.isEmpty()) {
                String query = "";
                for(int i = 0; i < conditionColumns.size(); i++) {
                    String conditionColumn = conditionColumns.get(i);
                    Object conditionValue = conditionValues.get(i);
                    query += conditionColumn + " = " + ((conditionValue instanceof Integer || conditionValue instanceof Long) ? conditionValue : ("'" + conditionValue + "'")) + (i == (conditionColumns.size() - 1) ? "" : " AND ");
                }
                return query;
            }
            return "";
        }
    }
    
    public static class ClearTableArguments extends Query {

        public ClearTableArguments(String tableName) {
            super(tableName);
        }

        @Override
        public String getQuery() {
            return "DELETE FROM " + getTableName() + ";";
        }

        @Override
        public boolean isCorrect() {
            return getTableName() != null;
        }
    }
    
    public static class DeleteArguments extends Query {

        private String conditionColumn = null;
        private Object conditionValue = null;

        public DeleteArguments(String tableName) {
            super(tableName);
        }

        public String getConditionColumn() {
            return conditionColumn;
        }

        public Object getConditionValue() {
            return conditionValue;
        }

        public DeleteArguments setCondition(String column, Object value) {
            this.conditionColumn = column;
            this.conditionValue = value;
            return this;
        }

        @Override
        public boolean isCorrect() {
            if (tableName == null || conditionColumn == null || conditionColumn.isEmpty() || conditionValue == null) {
                return false;
            }
            return true;
        }

        @Override
        public String getQuery() {
            String query = "DELETE FROM " + tableName + " ";
            if (conditionColumn != null && conditionValue != null) {
                query += "WHERE " + conditionColumn + " = " + ((conditionValue instanceof Integer || conditionValue instanceof Long) ? conditionValue : ("'" + conditionValue + "'"));
            }
            return query + ";";
        }
    }

    public static class InsertArguments extends Query {

        private LinkedList<String> columns = new LinkedList<>();
        private LinkedList<Object> values = new LinkedList<>();

        public InsertArguments addCoumnAndValue(String column, Object value) {
            if (column != null && !column.isEmpty() && value != null && !columns.contains(column)) {
                columns.add(column);
                values.add(value);
            }
            return this;
        }

        public InsertArguments(String tableName) {
            super(tableName);
        }

        @Override
        public boolean isCorrect() {
            return tableName != null && !tableName.isEmpty() && !columns.isEmpty() && !values.isEmpty() && columns.size() == values.size();
        }

        @Override
        public String getQuery() {
            String query = "INSERT INTO " + tableName + " (";
            int size = columns.size();
            for (String name : columns) {
                query += name + (size == 1 ? ")" : ", ");
                size--;
            }
            query += " VALUES (";
            size = values.size();
            for (Object obj : values) {
                query += (obj instanceof String ? "'" : "") + obj + (obj instanceof String ? "'" : "") + (size == 1 ? ")" : ", ");
                size--;
            }
            return query;
        }
    }
}


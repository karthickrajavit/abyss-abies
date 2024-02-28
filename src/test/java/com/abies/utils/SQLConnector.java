package com.abies.utils;

import java.sql.*;

public class SQLConnector {

    private static SQLConnector sqlConnector;

    public static SQLConnector getInstance(){
        if(sqlConnector == null){
            sqlConnector = new SQLConnector();
        }
        return sqlConnector;
    }

    public ResultSet connectToSQLAndReturnRecords(String sqlQuery){
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            String url = "jdbc:mysql://" + ConfigLoader.getInstance().getSqlHost() + ":" + ConfigLoader.getInstance().getSqlPort();
            String user = ConfigLoader.getInstance().getSqlLoginName();
            String password = ConfigLoader.getInstance().getSqlPassword();
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try{
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            if(!resultSet.next()){
                return resultSet;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultSet;
    }
}

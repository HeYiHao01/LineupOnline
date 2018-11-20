package com.example.lineuponline.Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    private static String dbUrl="jdbc:mysql://120.79.1.35:3306/account";
    private static String user="userAcc";
    private static String password="199808";
    private static String jdbcName="com.mysql.jdbc.Driver";

    public static Connection getCon() throws Exception{
        Class.forName(jdbcName);
        Connection con=DriverManager.getConnection(dbUrl, user, password);
        return con;
    }
    public void close(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }
}
package com.example.lineuponline.Util;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public static User login(Connection con, String user, String password)throws Exception{
        String sql="select * from account.user where userName=? and password=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setString(1, user);
        pstmt.setString(2, password);
        ResultSet rs=pstmt.executeQuery();
        User currentUser=null;
        if(rs.next()){
            currentUser=new User();
            currentUser.setId(rs.getInt("id"));
            currentUser.setName(rs.getString("userName"));
            currentUser.setPwd(rs.getString("password"));
            currentUser.setGender(rs.getString("gender"));
            currentUser.setAge(rs.getInt("age"));
            currentUser.setTel(rs.getString("tel"));
        }
        //con.close();
        return currentUser;
    }
    public static User login(Connection con, int userId)throws Exception{
        String sql="select * from account.user where id=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, userId);
        ResultSet rs=pstmt.executeQuery();
        User currentUser=null;
        if(rs.next()){
            currentUser=new User();
            currentUser.setId(rs.getInt("id"));
            currentUser.setName(rs.getString("userName"));
            currentUser.setPwd(rs.getString("password"));
            currentUser.setGender(rs.getString("gender"));
            currentUser.setAge(rs.getInt("age"));
            currentUser.setTel(rs.getString("tel"));
        }
        //con.close();
        return currentUser;
    }

    public static boolean islogin(Connection con, String user, String password)throws Exception{
        String sql="select * from account.user where userName=? and password=?";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setString(1, user);
        pstmt.setString(2, password);
        ResultSet rs=pstmt.executeQuery();
        return (rs.next());
    }

    public static int register(Connection con,User user)throws Exception{
        Log.d("test", "registering... ");
        String sql="select * from account.user where userName=? and tel=?";
        Log.d("test", "query...");
        PreparedStatement pstmt1=con.prepareStatement(sql);
        pstmt1.setString(1, user.getName());
        pstmt1.setString(2,user.getTel());
        ResultSet rs=pstmt1.executeQuery();
        Log.d("test", "query success... ");
        if(rs.next()){
            //return 2;
            Log.d("test", "has repeat");
            return 0;
        }else{
            Log.d("test", "try to register...");
            String maxId = "SELECT MAX(id) from account.user";
            PreparedStatement pstmt2=con.prepareStatement(maxId);
            ResultSet rs1=pstmt2.executeQuery();
            rs1.next();
            int newId = rs1.getInt(1)+1;
            Log.d("newId", String.valueOf(newId));
            String sql1="insert into account.user values("+newId+",?,?,?,?,?)";
            PreparedStatement pstmt=con.prepareStatement(sql1);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getGender());
            pstmt.setInt(3, user.getAge());
            pstmt.setString(4, user.getPwd());
            pstmt.setString(5, user.getTel());
            pstmt.executeUpdate();
            Log.d("test", "register succeed ");
            //con.close();
            return 1;
        }
    }
}

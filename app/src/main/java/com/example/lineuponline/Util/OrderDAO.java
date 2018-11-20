package com.example.lineuponline.Util;

import android.util.Log;
import android.widget.Toast;

import com.example.lineuponline.Reservation;
import com.example.lineuponline.UserInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderDAO {
    public static int reserve(Connection con, Order order)throws Exception{
        Log.d("test", "registering... ");
        //String sql="select * from account.reservation where id=? and userTel=?";
        String sql="select * from account.reservation where id=? and status = 'R'";
        Log.d("test", "query...");
        PreparedStatement pstmt1=con.prepareStatement(sql);
        pstmt1.setInt(1, order.getId());
        //pstmt1.setString(2,order.getUserTel());
        ResultSet rs=pstmt1.executeQuery();
        Log.d("test", "query success... ");
        if(rs.next()){
            //return 2;
            Log.d("test", "has repeat");
            return 0;
        }else{
            Log.d("test", "try to register...");
            String query_dno = "SELECT dno,hno,dept FROM account.doctor where dname = ?";
            PreparedStatement ps = con.prepareStatement(query_dno);
            ps.setString(1,order.getDoctor());
            ResultSet rsd = ps.executeQuery();

            rsd.next();
            String maxId = "SELECT MAX(position) from account.reservation where status='R' and dno=?";
            PreparedStatement pstmt2=con.prepareStatement(maxId);
            pstmt2.setString(1,rsd.getString("dno"));
            ResultSet rs1=pstmt2.executeQuery();
            rs1.next();
            int newPosition = rs1.getInt(1)+1;
            if (order.getReserve_date() != "" && order.getUserTel() != "") {
                Log.d("orderId", String.valueOf(order.getId()));
                Log.d("newPosition", String.valueOf(newPosition));
                Log.d("dno", rsd.getString("dno"));
                Log.d("hno", rsd.getString("hno"));
                Log.d("section", rsd.getString("dept"));
                Log.d("reserve_date", order.getReserve_date());
                String sql1 = "insert into account.reservation values(" + order.getId() + "," + newPosition + ",?,?,?,?,?)";
                PreparedStatement pstmt = con.prepareStatement(sql1);
                pstmt.setString(1, rsd.getString("dno"));
                pstmt.setString(2, rsd.getString("hno"));
                pstmt.setString(3, order.getUserTel());
                pstmt.setString(4, order.getReserve_date());
                pstmt.setString(5, "R");
                pstmt.execute();
                Log.d("test", "register succeed ");
                //con.close();
                return 1;
            }else {
                return -1;
            }
        }
    }

    public static Order query(Connection con,int orderId)throws Exception{
        String sql="select * from account.reservation where id=? and status = 'R'";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, orderId);
        ResultSet rs=pstmt.executeQuery();

        if (rs.next()){
            Log.d("dno", rs.getString("dno"));
            Log.d("hno", rs.getString("hno"));
            String query_dno = "SELECT dname,dept FROM account.doctor where dno = ?";
            String query_hno = "SELECT hname FROM account.hospital where hno = ?";
            PreparedStatement ps = con.prepareStatement(query_dno);
            PreparedStatement ps1 = con.prepareStatement(query_hno);
            ps.setString(1,rs.getString("dno"));
            ps1.setString(1,rs.getString("hno"));
            ResultSet rsd = ps.executeQuery();
            ResultSet rsh = ps1.executeQuery();

            Order currentUser=null;
            rsd.next();
            rsh.next();
            //if(rs.next()){
            currentUser=new Order();
            currentUser.setId(rs.getInt("id"));
            currentUser.setPosition(rs.getInt("position"));
            currentUser.setHospital(rsh.getString("hname"));
            currentUser.setSection(rsd.getString("dept"));
            currentUser.setDoctor(rsd.getString("dname"));
            //currentUser.setDoctor(rs.getString("dno"));
            currentUser.setUserTel(rs.getString("userTel"));
            currentUser.setReserve_date(rs.getString("reserve_date"));
            //}
            //con.close();
            return currentUser;
        }else {
            return null;
        }

    }

    public static ResultSet query_history(Connection con,int orderId)throws Exception{
        String sql="SELECT userName,gender,age,hname,dept,dname,reserve_date,status\n" +
                "from user,reservation,hospital,doctor \n" +
                "where user.id = reservation.id and user.id = ? and reservation.dno = doctor.dno\n" +
                "and hospital.hno = reservation.hno order by reserve_date;";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, orderId);
        ResultSet rs=pstmt.executeQuery();
        return rs;
        /*if (rs.next()){
            Record currentRecord = new Record();
            currentRecord.setName(rs.getString("userName"));
            currentRecord.setGender(rs.getString("gender"));
            currentRecord.setAge(rs.getInt("age"));
            currentRecord.setHospital(rs.getString("hname"));
            currentRecord.setSection(rs.getString("dept"));
            currentRecord.setDoctor(rs.getString("dname"));
            currentRecord.setDate(rs.getString("reserve_date"));
            if (rs.getString("status") == "R"){
                currentRecord.setStatus("预约中");
            }else if (rs.getString("status") == "C"){
                currentRecord.setStatus("取消");
            }else if (rs.getString("status") == "D"){
                currentRecord.setStatus("已完成");
            }
            return currentRecord;
        }else {
            return null;
        }*/
    }

    public static boolean cancel_res(Connection con,int orderId) throws Exception{
        String sql="update account.reservation set status = 'C' where id=? and status = 'R'";
        PreparedStatement pstmt=con.prepareStatement(sql);
        pstmt.setInt(1, orderId);
        return (!pstmt.execute());
    }
}

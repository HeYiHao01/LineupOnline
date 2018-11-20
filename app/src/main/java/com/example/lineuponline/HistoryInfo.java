package com.example.lineuponline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.OrderDAO;
import com.example.lineuponline.Util.Record;

import java.sql.Connection;
import java.sql.ResultSet;

public class HistoryInfo extends AppCompatActivity {
    private TextView name;
    private TextView gender;
    private TextView age;
    private TextView hospital;
    private TextView section;
    private TextView dname;
    private TextView date;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_info);
        name = (TextView)findViewById(R.id.history_name);
        gender = (TextView)findViewById(R.id.history_gender);
        age = (TextView)findViewById(R.id.history_age);
        hospital = (TextView)findViewById(R.id.history_hospital);
        section = (TextView)findViewById(R.id.history_section);
        dname = (TextView)findViewById(R.id.history_doctor);
        date = (TextView)findViewById(R.id.history_date);
        status = (TextView)findViewById(R.id.history_status);

        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        int position = mBundle.getInt("record");

        Log.d("record_position", String.valueOf(position));
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Connection conn = DBUtil.getCon();
            SharedPreferences shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
            int userId = shrf.getInt("id",0);
            ResultSet rs = OrderDAO.query_history(conn,userId);
            while ((position--) > -1){
                rs.next();
            }

            Record currentRecord = new Record();
            currentRecord.setName(rs.getString("userName"));
            currentRecord.setGender(rs.getString("gender"));
            currentRecord.setAge(rs.getInt("age"));
            currentRecord.setHospital(rs.getString("hname"));
            currentRecord.setSection(rs.getString("dept"));
            currentRecord.setDoctor(rs.getString("dname"));
            currentRecord.setDate(rs.getString("reserve_date"));
            Log.d("status", rs.getString("status"));
            if (rs.getString("status").equals("R")){
                currentRecord.setStatus("预约中");
            }else if (rs.getString("status").equals("C")){
                currentRecord.setStatus("取消");
            }else if (rs.getString("status").equals("D")){
                currentRecord.setStatus("已完成");
            }

            name.setText(currentRecord.getName());
            gender.setText(currentRecord.getGender());
            age.setText(String.valueOf(currentRecord.getAge()));
            hospital.setText(currentRecord.getHospital());
            section.setText(currentRecord.getSection());
            dname.setText(currentRecord.getDoctor());
            date.setText(currentRecord.getDate());
            status.setText(currentRecord.getStatus());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.example.lineuponline;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.Loading;
import com.example.lineuponline.Util.OrderDAO;
import com.example.lineuponline.Util.Record;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class History extends ListActivity {
    private ListView historyList;
    private List<String> historyData = new ArrayList<String>();
    private ArrayAdapter<String> historyAdapter;
    private Dialog dialog;

    private SharedPreferences shrf;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.no_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        dialog = Loading.createLoadingDialog(History.this,"查询中。。。");
        historyData.clear();
        shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
        userId = shrf.getInt("id",0);
        //new Thread(runnable).start();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Connection conn = DBUtil.getCon();
            ResultSet rs = OrderDAO.query_history(conn,userId);
            while (rs.next()){
                //Log.d("RS", "test test test");
                historyData.add(rs.getString("reserve_date"));
                //Log.d("date", rs.getString("reserve_date"));
            }
            setLayout();
            if (conn != null){
                rs.close();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Connection conn = DBUtil.getCon();
                ResultSet rs = OrderDAO.query_history(conn,userId);
                if (rs.next()){
                    historyData.add(rs.getString("reserve_date"));
                }
                setLayout();
                if (conn != null){
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

    private void setLayout(){
        if (!historyData.isEmpty()){
            setContentView(R.layout.history_list);
            historyList = (ListView)findViewById(android.R.id.list);
            historyAdapter = new ArrayAdapter<String>(this,R.layout.history_list_item,historyData);
            setListAdapter(historyAdapter);
            Loading.closeDialog(dialog);
            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(History.this,"敬请期待",Toast.LENGTH_SHORT).show();
                    Intent displayHistory = new Intent(History.this,HistoryInfo.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("record",position);
                    displayHistory.putExtras(mBundle);
                    startActivity(displayHistory);
                }
            });
        }else {
            Loading.closeDialog(dialog);
            setContentView(R.layout.no_history);
        }
    }
}

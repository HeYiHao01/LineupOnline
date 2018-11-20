package com.example.lineuponline;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.Order;
import com.example.lineuponline.Util.OrderDAO;
import com.example.lineuponline.Util.User;
import com.example.lineuponline.Util.UserDAO;

import java.sql.Connection;
import java.util.Calendar;

public class UserInfo extends BaseActivity {
    private TextView position;
    private TextView count;
    private TextView name;
    private TextView gender;
    private TextView age;
    private TextView hospital;
    private TextView section;
    private TextView dname;
    private TextView date;
    private TextView noInfo;

    private int id;
    //private SwipeRefreshLayout refreshLayout;
    //private RecyclerView recyclerView;
    private SharedPreferences pref;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private RecyclerView.Adapter adapter;
    private Button delete_res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        position = (TextView)findViewById(R.id.position);
        count = (TextView)findViewById(R.id.count);
        name = (TextView)findViewById(R.id.res_name);
        gender = (TextView)findViewById(R.id.res_gender);
        hospital = (TextView)findViewById(R.id.res_hospital);
        age = (TextView)findViewById(R.id.res_age);
        section = (TextView)findViewById(R.id.res_section);
        dname = (TextView)findViewById(R.id.res_doctor);
        date = (TextView)findViewById(R.id.res_date);

        delete_res = (Button)findViewById(R.id.delete_res);

        noInfo = (TextView)findViewById(R.id.noInfo);
        pref = getSharedPreferences("user",MODE_MULTI_PROCESS);
        id = pref.getInt("id",0);
        //refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        //recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        //InfoAdapter adapter = new InfoAdapter();
        //recyclerView.setAdapter(adapter);
        Log.d("reserveId", String.valueOf(id));
        //checkPosition();
        new Thread(runnable).start();
        //Log.d("orderPosition", position.getText().toString());
        //initInfo();
        /*refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshInfo();
            }
        });*/
        delete_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Connection conn = DBUtil.getCon();
                    AlertDialog dialog = showDeleteDialog(conn,id);
                    dialog.show();
                }catch (Exception e){

                }
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Log.d("Test", "test test test...");
                    try {
                        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        Connection conn = DBUtil.getCon();
                        Order order = OrderDAO.query(conn, id);
                        User user = UserDAO.login(conn,id);
                        if (order != null) {
                            //Log.d("orderPosition", String.valueOf(order.getPosition()));
                            Log.d("reserve_date", order.getReserve_date());
                            conn.close();
                            position.setText(String.valueOf(order.getPosition()));
                            count.setText(String.valueOf(order.getPosition() - 1));
                            date.setText(order.getReserve_date());
                            hospital.setText(order.getHospital());
                            section.setText(order.getSection());
                            dname.setText(order.getDoctor());
                            name.setText(user.getName());
                            gender.setText(user.getGender());
                            age.setText(String.valueOf(user.getAge()));
                            if (Integer.parseInt(count.getText().toString()) <= 3){
                                //Log.d("Test", "test test test...");
                                Intent checkPos = new Intent("com.example.admin.broardcastpractice.FORCE_OFFLINE");
                                sendBroadcast(checkPos);

                                Intent intent = new Intent(UserInfo.this, UserInfo.class);
                                PendingIntent sender = PendingIntent.getBroadcast(
                                        UserInfo.this, 0, intent, 0);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(Calendar.SECOND, 10);

                                // Schedule the alarm!
                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                            }
                        } else {
                            Log.d("Test", "test test test...");
                            Intent noInfo = new Intent(UserInfo.this,NoInfo.class);
                            finish();
                            startActivity(noInfo);
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private AlertDialog showDeleteDialog(final Connection con, final int orderId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认取消预约？").setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            boolean flag = OrderDAO.cancel_res(con, orderId);
                            if (flag){
                                Toast.makeText(UserInfo.this,"取消成功",Toast.LENGTH_SHORT).show();
                                finish();
                                Intent thisIntent = new Intent(UserInfo.this,UserInfo.class);
                                startActivity(thisIntent);
                            }else {
                                Toast.makeText(UserInfo.this,"取消失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    /*public void checkPosition(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (count.getText().toString().equals("1")){
                    Log.d("Test", "test test test...");
                    //intent = new Intent(UserInfo.this,UserInfo.class);
                    //PendingIntent pi = PendingIntent.getActivity(UserInfo.this,0,intent,0);
                    PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);
                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(context)
                            .setContentText("临近您的号啦！！！")
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.cross)
                            .setVibrate(new long[]{0,1000,1000,1000})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .build();
                    manager.notify(1,notification);
                }
            }
        };
    }*/

    /*private void refreshInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //initInfo();
                        //adapter.notifyDataSetChanged();

                        try {
                            Connection conn = DBUtil.getCon();
                            Order order = OrderDAO.query(conn, id);
                            if (order != null){
                                Log.d("orderPosition", String.valueOf(order.getPosition()));
                                conn.close();
                                position.setText(String.valueOf(order.getPosition()));
                                count.setText(String.valueOf(order.getPosition()-1));
                            }else {
                                position.setText("...");
                                count.setText("...");
                                noInfo.setText("无预约信息");
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }

                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }*/

    /*private void initInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Test", "test test test...");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Test", "test test test...");
                        try {
                            Connection conn = DBUtil.getCon();
                            Order order = OrderDAO.query(conn, id);
                            Log.d("Test", "test test test...");
                            if (order != null) {
                                Log.d("orderPosition", String.valueOf(order.getPosition()));
                                conn.close();
                                position.setText(String.valueOf(order.getPosition()));
                                count.setText(String.valueOf(order.getPosition() - 1));
                            } else {
                                position.setText("...");
                                count.setText("...");
                                noInfo.setText("无预约信息");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }*/
}

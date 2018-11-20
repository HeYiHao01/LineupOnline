package com.example.lineuponline;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.Loading;
import com.example.lineuponline.Util.UserDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends Activity{
    private EditText mName;
    private EditText mPassword;
    private Button mLoginButton;
    private TextView mRegisterLink;
    private SharedPreferences.Editor spEditor;
    private SharedPreferences shrf;
    private boolean isLogin;
    BroadcastReceiver broadcastReceiver;

    private Dialog logDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
        Log.d("login id", String.valueOf(shrf.getInt("id",0)));
        if (shrf.getInt("id",0) == 0) {

            setContentView(R.layout.activity_login);

            mName = (EditText) findViewById(R.id.name);
            mPassword = (EditText) findViewById(R.id.password);
            mLoginButton = (Button) findViewById(R.id.sign_in_button);
            mRegisterLink = (TextView) findViewById(R.id.register_link);

            mLoginButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    broadcastReceiver = null;
                    checkConnectivity();
                    logDialog = Loading.createLoadingDialog(LoginActivity.this,"登陆中...");
                    new Thread(runnable).start();
                }
            });

            mRegisterLink.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            try {
                Connection conn = DBUtil.getCon();
                if (!mName.getText().toString().equals("") && !mPassword.getText().toString().equals("")) {
                    isLogin = UserDAO.islogin(conn, mName.getText().toString(), mPassword.getText().toString());
                    Log.d("isLogin", String.valueOf(isLogin));
                    if (isLogin) {
                        spEditor = getSharedPreferences("user", MODE_MULTI_PROCESS).edit();
                        spEditor.putInt("id", UserDAO.login(conn, mName.getText().toString(), mPassword.getText().toString()).getId());
                        spEditor.putString("name", mName.getText().toString());
                        spEditor.putString("age", String.valueOf(UserDAO.login(conn, mName.getText().toString(), mPassword.getText().toString()).getAge()));
                        spEditor.putString("gender", UserDAO.login(conn, mName.getText().toString(), mPassword.getText().toString()).getGender());
                        spEditor.putString("tel", UserDAO.login(conn, mName.getText().toString(), mPassword.getText().toString()).getTel());
                        spEditor.apply();
                        conn.close();
                        Loading.closeDialog(logDialog);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Loading.closeDialog(logDialog);
                        Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Loading.closeDialog(logDialog);
                    Toast.makeText(LoginActivity.this, "请填写完整的正确信息", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            } catch (Exception e) {
                Log.d("login", "failure ");
                e.printStackTrace();
            }
        }
    };

    public void checkConnectivity() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                /*Bundle bundle = arg1.getExtras();
                NetworkInfo info = (NetworkInfo) bundle.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();
                if (state == NetworkInfo.State.CONNECTED) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No Connectivity", Toast.LENGTH_SHORT).show();
                }*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()){
                    //Toast.makeText(LoginActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "没有网络", Toast.LENGTH_SHORT).show();
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver,intentFilter);
    }
}
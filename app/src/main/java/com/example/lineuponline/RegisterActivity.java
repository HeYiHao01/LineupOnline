package com.example.lineuponline;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.Loading;
import com.example.lineuponline.Util.User;
import com.example.lineuponline.Util.UserDAO;

import java.sql.Connection;

public class RegisterActivity extends AppCompatActivity {
    private EditText mName;
    private EditText mPwd;
    private EditText mPwd2;
    //private RadioGroup mUserGender;
    private RadioButton male;
    private RadioButton female;
    private EditText mUserAge;
    private EditText mTel;
    private Button mRegister;
    private Dialog registerLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName = (EditText)findViewById(R.id.userName);
        mPwd = (EditText)findViewById(R.id.userPwd);
        mPwd2 = (EditText)findViewById(R.id.userPwd2);
        //mUserGender = (RadioGroup)findViewById(R.id.userGender);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        mUserAge = (EditText)findViewById(R.id.userAge);
        mTel = (EditText)findViewById(R.id.userTel);
        mRegister = (Button)findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLog = Loading.createLoadingDialog(RegisterActivity.this,"注册中...");
                if (mPwd2.getText().toString().equals(mPwd.getText().toString())) {
                    if (mName.getText().toString() != "" && mUserAge.getText().toString() != ""
                            && mTel.getText().toString() != "" && mTel.getText().length() == 11){
                        new Thread(runnable).start();
                    }else {
                        Toast.makeText(RegisterActivity.this,"请填写有效信息",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"密码不匹配",Toast.LENGTH_SHORT).show();
                }
                Loading.closeDialog(registerLog);
                /*if (mPwd2.getText().toString().equals(mPwd.getText().toString())) {
                    SharedPreferences.Editor editor = getSharedPreferences("user", MODE_MULTI_PROCESS).edit();
                    editor.putString("name", mName.getText().toString());
                    editor.putString("password", mPwd.getText().toString());
                    editor.putString("tel", mTel.getText().toString());
                    editor.putBoolean("remberPwd",false);
                    editor.apply();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterActivity.this,"密码不匹配",Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Connection conn = DBUtil.getCon();
                Log.d("test", "connecting success ");
                User user;
                if (male.isChecked()){
                    user = new User(mName.getText().toString(),mPwd.getText().toString(),male.getText().toString(),Integer.parseInt(mUserAge.getText().toString()),mTel.getText().toString());
                }else {
                    user = new User(mName.getText().toString(),mPwd.getText().toString(),female.getText().toString(),Integer.parseInt(mUserAge.getText().toString()),mTel.getText().toString());
                }
                int isRegistered = UserDAO.register(conn,user);
                if (isRegistered == 0){
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this,"注册失败，或已注册过",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else{
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this,"注册成功,两秒跳转登陆。。。",Toast.LENGTH_SHORT).show();
                    Thread.sleep(1500);
                    conn.close();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}

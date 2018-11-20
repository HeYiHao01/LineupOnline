package com.example.lineuponline;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.DownloadService;
import com.example.lineuponline.Util.Loading;
import com.example.lineuponline.Util.User;
import com.example.lineuponline.Util.UserDAO;

import java.io.File;
import java.sql.Connection;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainActivity extends AppCompatActivity {
    //private RelativeLayout relativeLayout;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences shrf;
    private SharedPreferences.Editor spEditor;
    private Button book;
    private Button info;
    private TextView name;
    private TextView age;
    private TextView tel;
    private int id;

    private static final int INSTALL_PERMISS_CODE = 123;
    private Dialog updateDialog;

    //private Handler handler;
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handler = new Handler();
        //new Thread(runnable).start();
        setContentView(R.layout.activity_main);
        shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
        spEditor = getSharedPreferences("user",MODE_MULTI_PROCESS).edit();
        id = shrf.getInt("id",0);
        //Log.d("main id", String.valueOf(id));
        //relativeLayout = (RelativeLayout)findViewById(R.id.head);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        book = (Button)findViewById(R.id.book);
        info = (Button)findViewById(R.id.info);
        name = (TextView)findViewById(R.id.username);
        age = (TextView)findViewById(R.id.userage);
        tel = (TextView)findViewById(R.id.usertel);

        name.setText(shrf.getString("name",""));
        tel.setText(shrf.getString("tel",""));

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        /*Intent intent = new Intent(getApplicationContext(),DownloadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else {
            startService(intent);
        }
        bindService(intent,connection,BIND_AUTO_CREATE);
        Log.d("bindService", String.valueOf(bindService(intent,connection,BIND_AUTO_CREATE)));*/

        navView.setCheckedItem(R.id.nav_call);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.nav_call){
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    //intent.setData(Uri.parse("tel:18852894644"));
                    intent.setData(Uri.parse("tel:17353763418"));
                    startActivity(intent);
                }else if (menuItem.getItemId() == R.id.nav_logout){
                    spEditor.clear();
                    spEditor.apply();
                    finish();
                    Intent logout = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(logout);
                }else if (menuItem.getItemId() == R.id.nav_about){
                    //about us
                    mDrawerLayout.closeDrawers();
                    Intent aboutUs = new Intent(MainActivity.this,AboutUs.class);
                    startActivity(aboutUs);
                } else if (menuItem.getItemId() == R.id.nav_history){
                    //查询历史记录
                    mDrawerLayout.closeDrawers();
                    //Log.d("userId", String.valueOf(shrf.getInt("id",0)));
                    //Toast.makeText(MainActivity.this,"暂无历史纪录",Toast.LENGTH_SHORT).show();
                    /*try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(MainActivity.this,History.class);
                    startActivity(intent);
                }else if (menuItem.getItemId() == R.id.nav_update){
                    Intent intent = new Intent(getApplicationContext(),DownloadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }else {
                        startService(intent);
                    }
                    bindService(intent,connection,BIND_AUTO_CREATE);
                    Log.d("bindService", String.valueOf(bindService(intent,connection,BIND_AUTO_CREATE)));

                    mDrawerLayout.closeDrawers();

                    updateDialog = Loading.createLoadingDialog(MainActivity.this,"检查更新。。。");
                    //检查更新
                    //Toast.makeText(MainActivity.this,"暂无更新版本",Toast.LENGTH_SHORT).show();

                    /*if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 2);
                    }*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getPackageManager().canRequestPackageInstalls()){
                            /*Intent reqPer = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                            startActivityForResult(reqPer,1);*/
                            Loading.closeDialog(updateDialog);
                            setInstallPermission();
                        }else {
                            String url = "http://www.hyhzcc.xin/reservationV2-5-4.apk";
                            //downloadBinder.startDownload(url);
                            if (downloadBinder != null){
                                downloadBinder.startDownload(url);
                                Loading.closeDialog(updateDialog);
                            }else {
                                Loading.closeDialog(updateDialog);
                                Toast.makeText(MainActivity.this,"暂无更新版本",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    //Loading.closeDialog(updateDialog);
                }
                return true;
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reserve = new Intent(MainActivity.this,Reservation.class);
                startActivity(reserve);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(MainActivity.this,UserInfo.class);
                startActivity(info);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限则无法更新", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    /*protected void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.example.lineuponline.fileprovider", file);  //包名.fileprovider
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);

    }*/
    /**
     * 8.0以上系统设置安装未知来源权限
     */
    public void setInstallPermission(){
        boolean haveInstallPermission;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //先判断是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if(!haveInstallPermission){
                //弹框提示用户手动打开
                /*MessageDialog.showAlert(this, "安装权限", "需要打开允许来自此来源，请去设置中开启此权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //此方法需要API>=26才能使用
                            toInstallPermissionSettingIntent();
                        }
                    }
                });*/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("安装权限");
                builder.setMessage("需要打开允许来自此来源，请去设置中开启此权限");
                builder.setIcon(R.drawable.cross);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            toInstallPermissionSettingIntent();
                        }
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.create().show();
                return;
            }
        }
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:"+getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
        startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISS_CODE) {
            String url = "http://www.hyhzcc.xin/reservationV2-4.apk";
            //downloadBinder.startDownload(url);
            if (downloadBinder != null){
                downloadBinder.startDownload(url);
            }else {
                Toast.makeText(MainActivity.this,"暂无更新版本",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Connection conn = DBUtil.getCon();
                User user = UserDAO.login(conn, id);
                conn.close();
                String uName = user.getName();
                String uTel = user.getTel();
                name.setText(uName);
                tel.setText(uTel);*/
                /*Log.d("user", uName + "---" + uTel);
                Intent intent = new Intent(MainActivity.this,UserInfo.class);
                intent.putExtra("userName",uName);
                intent.putExtra("userTel",uTel);
                startActivity(intent);*/
            /*} catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

}

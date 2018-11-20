package com.example.lineuponline.Util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.example.lineuponline.MainActivity;
import com.example.lineuponline.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    private String downloadUrl;

    private static final String PUSH_CHANNEL_ID = "DOWNLOAD_ID";
    private static final String PUSH_CHANNEL_NAME = "DOWNLOAD_SERVICE";

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();

            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory+fileName);
            installApk(file);
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            //getNotificationManager().notify(1,getNotification("Download Failed",-1));
            Toast.makeText(DownloadService.this,"已经是最新版啦",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this,"Paused",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_SHORT).show();
        }
    };

    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder{
        public void startDownload(String url){
            if (downloadTask == null){
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);

                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    if (manager != null) {
                        manager.createNotificationChannel(channel);
                    }
                    //manager.notify(1,getNotification("Downloading...",0));
                    startForeground(1,getNotification("Downloading...",0));
                }
                Toast.makeText(DownloadService.this,"Downloading...",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(DownloadService.this,"暂无更新",Toast.LENGTH_SHORT).show();
            }
        }
        public void pauseDownload(){
            if (downloadTask != null){
                downloadTask.pauseDownload();
            }
        }
        public void cancelDownload(){
            if (downloadTask != null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl != null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory+fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();;
                }
            }
        }
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title,int progress){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_MAX);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }*/

            if (progress > 0) {
                Notification notification = new NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pi)
                        .setSmallIcon(R.drawable.cross)
                        .setLargeIcon(getBitmap(DownloadService.this,R.drawable.cross))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(progress + "%")
                        .setProgress(100, progress, false)
                        .build();
                return notification;
            } else {
                Notification notification = new Notification.Builder(this, PUSH_CHANNEL_ID)
                        .setChannelId("update")
                        .setContentTitle(title)
                        .setContentIntent(pi)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(getBitmap(DownloadService.this, R.mipmap.ic_launcher))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setChannelId(PUSH_CHANNEL_ID)
                        .build();
                return notification;
            }
        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            //builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.cross));
            builder.setLargeIcon(getBitmap(DownloadService.this, R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle(title);
            if (progress > 0) {
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
            return builder.build();
        }
        //manager.notify(1,notification);

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.cross);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.cross));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress > 0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();*/
    }

    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap=null;
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    protected void installApk(File file) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
                    Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.lineuponline.fileprovider", file);  //包名.fileprovider
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                startActivity(intent);
            }

        }
    }
    /*public static void showAlert(Context context, String title, String message, OnClickListener listener){
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", listener);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}

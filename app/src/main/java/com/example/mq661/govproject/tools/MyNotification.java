package com.example.mq661.govproject.tools;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.mq661.govproject.R;


public class MyNotification extends ContextWrapper {
    //final static String GROUP_KEY_EMAILS = "group_key_emails";
    public MyNotification(Context base) {
        super(base);
    }

    public void MyNotification(String ContentTitle, String ContentText, int LargeIcon, String channellid, String channellname, int id, String group) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = channellid;
            String channelName = channellname;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }

        if (!mynotify.isNotificationEnabled(this))//没有开启权限
        {
            toSetting();
            Toast.makeText(this, "请手动将通知权限打开", Toast.LENGTH_SHORT).show();


            //          final AlertDialog dialog = new AlertDialog.Builder(this).create();
//            dialog.show();
//            View view = View.inflate(this, R.tabhost_layout.dialog, null);
//            dialog.setContentView(view);
//            TextView context = (TextView) view.findViewById(R.id.tv_dialog_context);
//            context.setText("检测到您没有打开通知权限，是否去打开");
//            TextView confirm = (TextView) view.findViewById(R.id.btn_confirm);
//            confirm.setText("确定");
//            confirm.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    dialog.cancel();
//                    Intent localIntent = new Intent();
//                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    if (Build.VERSION.SDK_INT >= 9) {//系统8.0以上的
//                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//                    } else if (Build.VERSION.SDK_INT <= 8) {//系统8.0以下的
//                        localIntent.setAction(Intent.ACTION_VIEW);
//                        localIntent.setClassName("com.android.settings",
//                                "com.android.settings.InstalledAppDetails");
//                        localIntent.putExtra("com.android.settings.ApplicationPkgName",
//                               getPackageName());
//                    }
//                    startActivityForResult(localIntent, REQUESTCODE);
//                }
//            });
//
//            TextView cancel = (TextView) view.findViewById(R.id.btn_off);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    dialog.cancel();
//                }
//            });


//            AlertDialog.Builder normalDialog =
//                    new AlertDialog.Builder(getApplicationContext());
//            normalDialog.setIcon(R.drawable.app);
//            normalDialog.setTitle("GoV").setMessage("检测到您没有打开通知权限，是否去打开");
//
//            normalDialog.setPositiveButton("取消",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//
//            normalDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    toSetting();
//                }
//            });
//
//            // 创建实例并显示
//            normalDialog.show();

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = manager.getNotificationChannel(channellid);
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {

                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                    Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
                } else {
                    Notification notification = new NotificationCompat.Builder(this, channellid)
                            .setContentTitle(ContentTitle)
                            .setContentText(ContentText)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.iconsmall)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), LargeIcon))
                            .setAutoCancel(true)
                            .setPriority(2)
                            .setGroup(group)
                            .build();

                    manager.notify(id, notification);
                }
            } else {
                Notification notification = new NotificationCompat.Builder(this, channellid)
                        .setContentTitle(ContentTitle)
                        .setContentText(ContentText)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.iconsmall)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), LargeIcon))
                        .setAutoCancel(true)
                        .setTicker("悬浮通知")
                        .setDefaults(~0)
                        .setGroup(group)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .build();
                notification.defaults |= Notification.DEFAULT_SOUND;

                manager.notify(id, notification);

            }
        }
    }

    private void toSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
//    public void initNotice() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "chat";
//            String channelName = channellname;
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            createNotificationChannel(channelId, channelName, importance);
//
//            channelId = "subscribe";
//            channelName = "订阅消息";
//            importance = NotificationManager.IMPORTANCE_DEFAULT;
//            createNotificationChannel(channelId, channelName, importance);
//        }
//    }
}

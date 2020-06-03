package com.native_code.bp_project02.Chat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.native_code.bp_project02.R;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG="FirebaseMsgService";

    private String msg,title;

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("FCM Log","Refreshed token"+s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(TAG,getRunActivity());
        //메세지 액티비티 일 시 알림 메세지 보내지 않음
        if(!getRunActivity().equals("com.native_code.bp_project02.Chat.MessageActivity")) {
            sendNotification(remoteMessage);
        }


    }
    private void sendNotification (RemoteMessage remoteMessage) {

        //Log.d("FCM log", "알림 메시지" + remoteMessage.getNotification().getBody());
        //String messageBody = remoteMessage.getNotification().getBody();
        //String messageTitle = remoteMessage.getNotification().getTitle();



        Log.e(TAG,"messageData: "+ remoteMessage.getData());
        String messageBody=remoteMessage.getData().get("text");
        String messageTitle=remoteMessage.getData().get("title");
        String send_uid=remoteMessage.getData().get("send_uid");


        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("destination_Uid", send_uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.loding_icons)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            //API 26부터 채널생성 가능


        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private String getRunActivity()	{

        ActivityManager activity_manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> task_info = activity_manager.getRunningTasks(9999);


        return task_info.get(0).topActivity.getClassName();

    }



}

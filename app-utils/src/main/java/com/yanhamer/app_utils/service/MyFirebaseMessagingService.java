package com.yanhamer.app_utils.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Create NotificationBuilder to init MyFirebaseMessagingService Object
 * and set params
 * @channel_id NotificationChannel id to send notification
 * @small_icon set small icon of NotificationCompat builder next to title
 * @large_icon set large icon of NotificationCompat builder in body notif.
 *
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String channel_id;
    private int small_icon, large_icon;

    public MyFirebaseMessagingService(NotificationBuilder builder) {
        this.channel_id = builder.channel_id;
        this.small_icon = builder.small_icon;
        this.large_icon = builder.large_icon;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getBody() != null) {
                //Log.e("FIREBASE", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage);
            }
        }
    }

    /**
     * when receive notification run this method to build notification
     * with the received content
     * @param remoteMessage content of notification (title & body)
     *
     */
    private void sendNotification(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        String link = remoteMessage.getData().get("url");
        Bundle bundle = new Bundle();
        bundle.putString("url", link);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse(link));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert notification != null;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                channel_id)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), large_icon))
                .setSmallIcon(small_icon)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * create your own builder with your requirements
     * that belongs to your project
     *
     */
    public static class NotificationBuilder{

        private String channel_id;
        private int small_icon, large_icon;

        public NotificationBuilder(){}

        public NotificationBuilder setChannelID(String channelID){
            this.channel_id = channelID;
            return this;
        }

        public NotificationBuilder setSmallIcon(int smallIcon){
            this.small_icon = smallIcon;
            return this;
        }

        public NotificationBuilder setLargeIcon(int largeIcon){
            this.large_icon = largeIcon;
            return this;
        }

        public MyFirebaseMessagingService build(){
            return new MyFirebaseMessagingService(this);
        }

    }

}

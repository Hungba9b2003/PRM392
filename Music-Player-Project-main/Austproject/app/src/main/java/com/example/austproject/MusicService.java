//package com.example.austproject;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Build;
//import android.os.IBinder;
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//
//public class MusicService extends Service {
//    private MediaPlayer mediaPlayer;
//    private static final String CHANNEL_ID = "MusicServiceChannel";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music); // Thay bằng bài hát của bạn
//        mediaPlayer.setLooping(true);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        createNotificationChannel();
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Đang phát nhạc")
//                .setContentText("Nhạc đang chạy ngay cả khi bạn thoát ứng dụng")
//                .setSmallIcon(R.drawable.ic_music)
//                .build();
//        startForeground(1, notification);
//
//        mediaPlayer.start();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Music Service Channel",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) {
//                manager.createNotificationChannel(serviceChannel);
//            }
//        }
//    }
//}

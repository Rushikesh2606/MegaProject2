package com.example.codebrains.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.codebrains.Homepage;
import com.example.codebrains.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class FirebaseMessageListenerService extends Service {

    private static final String TAG = "FirebaseMsgService";
    private static final String CHANNEL_ID = "message_notifications_channel";
    private static final String PREFS_NAME = "MyServicePrefs";
    private static final String KEY_LAST_SERVICE_END_TIME = "lastServiceEndTime";

    private DatabaseReference chatsRef;
    private FirebaseAuth mAuth;

    private Set<String> notifiedMessageIds = new HashSet<>();
    private long lastServiceEndTime;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Message listening service started...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Service started");

        createNotificationChannel();
        startForeground(1, getServiceNotification());

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getUid();
        if (currentUserId == null) {
            stopSelf();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastServiceEndTime = prefs.getLong(KEY_LAST_SERVICE_END_TIME, 0);

        chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        // Listen for ALL chat threads (existing ones)
        chatsRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            for (DataSnapshot chatThreadSnapshot : snapshot.getChildren()) {
                String chatKey = chatThreadSnapshot.getKey(); // Format: sender_receiver
                if (chatKey == null || !chatKey.contains("_")) continue;

                String[] ids = chatKey.split("_");
                if (ids.length != 2) continue;

                String senderId = ids[0];
                String receiverId = ids[1];

                if (!receiverId.equals(currentUserId)) continue;

                // Attach child listener to each message inside this thread
                DatabaseReference messagesRef = chatsRef.child(chatKey);
                messagesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot messageSnapshot, String previousChildName) {
                        String messageId = messageSnapshot.getKey();
                        String uId = messageSnapshot.child("uId").getValue(String.class);
                        String message = messageSnapshot.child("message").getValue(String.class);
                        Long timeStamp = messageSnapshot.child("timeStamp").getValue(Long.class);

                        if (messageId == null || uId == null || message == null || timeStamp == null) return;

                        if (!uId.equals(currentUserId) && !notifiedMessageIds.contains(messageId)) {
                            Log.d(TAG, "New message from: " + senderId + " -> " + message);
                            sendNotification("New Message", "From: " + senderId + "\n" + message);
                            notifiedMessageIds.add(messageId);

                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putLong(KEY_LAST_SERVICE_END_TIME, System.currentTimeMillis());
                            editor.apply();
                        }
                    }

                    @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}
                    @Override public void onChildRemoved(DataSnapshot snapshot) {}
                    @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
                    @Override public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Message listener error: " + error.getMessage());
                    }
                });
            }
        });
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, Homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message.length() > 40 ? message.substring(0, 40) + "..." : message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.d(TAG, "Notifying user with: " + title + " - " + message);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Message Notifications";
            String description = "Notifications for new messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification getServiceNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("CodeBrains")
                .setContentText("Listening for new messages...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

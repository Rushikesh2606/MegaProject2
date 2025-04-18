package com.example.codebrains.services;

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

import com.example.codebrains.Homepage_developer;
import com.example.codebrains.MainActivity;
import com.example.codebrains.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class FirebaseConnectionService extends Service {

    private static final String TAG = "FirebaseConnectionSvc";
    private static final String CHANNEL_ID = "connection_notifications_channel";
    private static final String PREFS_NAME = "ConnectionPrefs";
    private static final String KEY_LAST_CHECK_TIME = "lastConnectionCheckTime";

    private DatabaseReference connectionsRef;
    private ChildEventListener connectionChildListener;
    private FirebaseAuth mAuth;

    private Set<String> notifiedConnectionIds = new HashSet<>();
    private long lastConnectionCheckTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Connection service is running...", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        createNotificationChannel();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastConnectionCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0);

        connectionsRef = FirebaseDatabase.getInstance().getReference("Connections");

        connectionChildListener = connectionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.exists()) return;

                Long timestamp = snapshot.child("timeStamp").getValue(Long.class);
                if (timestamp == null) timestamp = 0L;

                String connectionId = snapshot.getKey();
                String freelancerId = snapshot.child("freelancer").getValue(String.class);

                Log.d(TAG, "Connection timestamp: " + timestamp + ", stored lastConnectionCheckTime: " + lastConnectionCheckTime);

                if (timestamp > lastConnectionCheckTime && connectionId != null && !notifiedConnectionIds.contains(connectionId)) {
                    String currentUserId = mAuth.getUid();
                    Log.d(TAG, "CurrentUser: " + currentUserId + ", freelancerId: " + freelancerId);

                    if (currentUserId != null && currentUserId.equals(freelancerId)) {
                        // ✅ Send notification
                        sendNotification("New Connection", "Your bid has been accepted!");
                        notifiedConnectionIds.add(connectionId);

                        // ✅ Save updated time

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putLong(KEY_LAST_CHECK_TIME, System.currentTimeMillis());
                        editor.apply();

                        Log.d(TAG, "✅ Notification sent for connectionId: " + connectionId + ", updated lastConnectionCheckTime: " + timestamp);
                    } else {
                        Log.d(TAG, "👤 Skipped connection not related to current user.");
                    }
                } else {
                    Log.d(TAG, "⏭ Skipped old or duplicate connection.");
                }
            }

            @Override public void onChildChanged(DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot snapshot) {}
            @Override public void onChildMoved(DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {
                Log.e(TAG, "❌ Firebase listener cancelled: " + error.getMessage());
            }
        });
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, Homepage_developer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Connection Notifications";
            String description = "Notifies freelancers of accepted bids (new connections)";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "🛑 FirebaseConnectionService destroyed.");
        if (connectionsRef != null && connectionChildListener != null) {
            connectionsRef.removeEventListener(connectionChildListener);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

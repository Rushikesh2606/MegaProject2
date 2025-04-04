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

import com.example.codebrains.Homepage;
import com.example.codebrains.R;
import com.example.codebrains.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class FirebaseProposalListenerService extends Service {
    private static final String TAG = "FirebaseProposalService";
    private static final String CHANNEL_ID = "proposal_notifications_channel";
    private static final String PREFS_NAME = "MyServicePrefs";
    private static final String KEY_LAST_SERVICE_END_TIME = "lastServiceEndTime";

    private DatabaseReference proposalsRef;
    private ChildEventListener proposalChildListener;
    private FirebaseAuth mAuth;

    private Set<String> notifiedJobIds = new HashSet<>();
    private long lastServiceEndTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "All services are running...", Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        createNotificationChannel();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastServiceEndTime = prefs.getLong(KEY_LAST_SERVICE_END_TIME, 0);

        proposalsRef = FirebaseDatabase.getInstance().getReference("proposals");

        proposalChildListener = proposalsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if (dataSnapshot.exists()) {
                    Long proposalTimestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                    if (proposalTimestamp == null) proposalTimestamp = 0L;

                    Log.d(TAG, "Proposal timestamp: " + proposalTimestamp + ", stored lastServiceEndTime: " + lastServiceEndTime);

                    // If the proposal is older or equal, allow notification
                    if (proposalTimestamp > lastServiceEndTime) {
                        String jobId = dataSnapshot.child("jobId").getValue(String.class);
                        if (jobId != null && !notifiedJobIds.contains(jobId)) {
                            DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);
                            Long finalProposalTimestamp = proposalTimestamp;
                            jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot jobSnapshot) {
                                    if (jobSnapshot.exists()) {
                                        String jobUsername = jobSnapshot.child("username").getValue(String.class);
                                        String currentUserId = mAuth.getUid();
                                        if (currentUserId != null && currentUserId.equals(jobUsername)) {
                                            // ✅ Send notification
                                            sendNotification("New Proposal Received",
                                                    "A new proposal has been added for your job ");
                                            notifiedJobIds.add(jobId);

                                            // ✅ Save updated time
                                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                            prefs.edit().putLong(KEY_LAST_SERVICE_END_TIME, System.currentTimeMillis()).apply();
                                            Log.d(TAG, "✅ Updated lastServiceEndTime to: " + finalProposalTimestamp);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Job lookup cancelled: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            Log.d(TAG, "Notification already sent for jobId: " + jobId);
                        }
                    } else {
                        Log.d(TAG, "Skipping proposal with timestamp " + proposalTimestamp + " (too old)");
                    }


                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Proposal listener cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, Homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Proposal Notifications";
            String description = "Notifications for new proposals";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("done", "Called the onDestroy");
        if (proposalsRef != null && proposalChildListener != null) {
            proposalsRef.removeEventListener(proposalChildListener);
        }
        // 🔁 No need to update lastServiceEndTime here anymore, handled after notification
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

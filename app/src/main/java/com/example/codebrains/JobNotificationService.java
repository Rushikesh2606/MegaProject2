package com.example.codebrains;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.codebrains.model.JobController;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobNotificationService extends Service {
    private static final String CHANNEL_ID = "JobNotifications";
    private static final long CHECK_INTERVAL = 5000; // 5 seconds
    private static final String TAG = "JobNotificationService";
    private static final String PREF_LAST_NOTIFIED_TIME = "last_notified_time";

    private Handler handler;
    private Runnable checkJobsRunnable;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String profession = sp.getString("profession", "");

        if (profession.equals("Freelancer")) {
            initializePeriodicChecks();
        } else {
            stopSelf();
        }
    }

    private void initializePeriodicChecks() {
        handler = new Handler(Looper.getMainLooper());

        checkJobsRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewJobs();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };

        handler.post(checkJobsRunnable);
    }

    private void checkForNewJobs() {
        long lastNotifiedTime = sp.getLong(PREF_LAST_NOTIFIED_TIME, 0);

        FirebaseDatabase.getInstance().getReference("jobs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            JobController job = jobSnapshot.getValue(JobController.class);
                            if (job != null) {
                                // Ensure postedTimestamp is a long
                                long postedTimestamp = job.getPostedTimestamp();

                                if (postedTimestamp > lastNotifiedTime && isJobMatching(jobSnapshot)) {
                                    sendNotification(job);
                                    // Update the last notified time
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putLong(PREF_LAST_NOTIFIED_TIME, postedTimestamp);
                                    editor.apply();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                    }
                });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Job Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.setLightColor(getResources().getColor(android.R.color.holo_blue_light));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private boolean isJobMatching(DataSnapshot snapshot) {
        String developerSkills = sp.getString("developer_skills", "");
        if (developerSkills == null || developerSkills.isEmpty()) {
            return false;
        }

        JobController job = snapshot.getValue(JobController.class);
        if (job == null) return false;

        String jobCategory = job.getJobCategory();
        if (jobCategory == null || jobCategory.isEmpty()) {
            return false;
        }

        String[] skillsArray = developerSkills.split(",");
        for (String skill : skillsArray) {
            if (jobCategory.equalsIgnoreCase(skill.trim())) {
                return true;
            }
        }
        return false;
    }

    private void sendNotification(JobController job) {
        Intent intent = new Intent(this, Homepage_developer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Job Posted!")
                .setContentText(job.getJobTitle() + " - " + job.getJobCategory())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setColor(getResources().getColor(android.R.color.holo_blue_light))
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(checkJobsRunnable);
        }
    }
}
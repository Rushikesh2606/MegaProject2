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
import com.example.codebrains.model.Reevaluation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class JobNotificationService extends Service {
    private static final String CHANNEL_ID = "JobNotifications";
    private static final long CHECK_INTERVAL = 5000;
    private static final String TAG = "JobNotificationService";
    private static final String PREF_LAST_NOTIFIED_JOB_TIME = "last_notified_job_time";
    private static final String PREF_LAST_NOTIFIED_REEVAL_TIME = "last_notified_reeval_time";

    private Handler handler;
    private Runnable checkRunnable;
    private SharedPreferences sp;
    private String currentUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String profession = sp.getString("profession", "");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            stopSelf();
            return;
        }

        if (profession.equals("Freelancer")) {
            initializePeriodicChecks();
        } else {
            stopSelf();
        }
    }

    private void initializePeriodicChecks() {
        handler = new Handler(Looper.getMainLooper());

        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewJobs();
                checkForNewReevaluations();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };

        handler.post(checkRunnable);
    }

    private void checkForNewJobs() {
        long lastTime = sp.getLong(PREF_LAST_NOTIFIED_JOB_TIME, 0);

        FirebaseDatabase.getInstance().getReference("jobs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            JobController job = jobSnapshot.getValue(JobController.class);
                            if (job != null && job.getPostedTimestamp() > lastTime && isJobMatching(jobSnapshot)) {
                                sendJobNotification(job);

                                sp.edit()
                                        .putLong(PREF_LAST_NOTIFIED_JOB_TIME, job.getPostedTimestamp())
                                        .apply();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error loading jobs: " + error.getMessage());
                    }
                });
    }

    private void checkForNewReevaluations() {
        long lastTime = sp.getLong(PREF_LAST_NOTIFIED_REEVAL_TIME, 0);

        FirebaseDatabase.getInstance().getReference("Reevaluated")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot reevalSnapshot : snapshot.getChildren()) {
                            Reevaluation reevaluation = reevalSnapshot.getValue(Reevaluation.class);
                            if (reevaluation == null) continue;

                            long timestamp = reevaluation.getTimestamp();
                            String freelancerId = reevaluation.getFreelancer();

                            if (timestamp > lastTime && currentUserId.equals(freelancerId)) {
                                sendReevaluationNotification(reevaluation);

                                sp.edit()
                                        .putLong(PREF_LAST_NOTIFIED_REEVAL_TIME, timestamp)
                                        .apply();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error loading reevaluations: " + error.getMessage());
                    }
                });
    }

    private boolean isJobMatching(DataSnapshot snapshot) {
        String developerSkills = sp.getString("developer_skills", "");
        if (developerSkills == null || developerSkills.isEmpty()) return false;

        JobController job = snapshot.getValue(JobController.class);
        if (job == null || job.getJobCategory() == null) return false;

        String[] skillsArray = developerSkills.split(",");
        for (String skill : skillsArray) {
            if (job.getJobCategory().equalsIgnoreCase(skill.trim())) {
                return true;
            }
        }
        return false;
    }

    private void sendJobNotification(JobController job) {
        Intent intent = new Intent(this, Homepage_developer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Job Available!")
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

    private void sendReevaluationNotification(Reevaluation reevaluation) {
        Intent intent = new Intent(this, Homepage_developer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("You’ve been Re-evaluated!")
                .setContentText("Reason: " + reevaluation.getReason())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setColor(getResources().getColor(android.R.color.holo_blue_dark))
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Job Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
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
            handler.removeCallbacks(checkRunnable);
        }
    }
}

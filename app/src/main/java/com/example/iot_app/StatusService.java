package com.example.iot_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.iot_app.home_page.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusService extends Service {
    private DatabaseReference sosRef;
    private NotificationManager notificationManager;
    private Ringtone ringtone;
    private final String notificationChannelId = "SOSStatusChannel";
    private final int sosStatusNotificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        sosRef = FirebaseDatabase.getInstance().getReference("rooms").child("Kho Linh Xuan").child("SOS");
        Log.d("đã connect với Service", String.valueOf(sosRef));
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSoundUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    notificationChannelId,
                    "SOS Status Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for sos status notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(channel);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                .setContentTitle("SOS Status")
                .setContentText("Checking SOS status...")
                .setSmallIcon(R.drawable.cold_storage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(sosStatusNotificationId, notificationBuilder.build());
        sosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Get the data from the snapshot
                String sosStatus = snapshot.getValue(String.class);
                Log.d("value_powerStatus", "Value is: " + sosStatus);
                if ("true".equals(sosStatus)) {
                    Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                            .setContentTitle("SOS Status")
                            .setContentText("The temperature is exceeding the allowable threshold")
                            .setSmallIcon(R.drawable.error)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);
                    notificationManager.notify(sosStatusNotificationId, notificationBuilder.build());

                     if (!ringtone.isPlaying()) {
                         ringtone.play();
                     }
                } else {
                    notificationBuilder.setContentText("The temperature is within the allowable range");
                    notificationBuilder.setSmallIcon(R.drawable.cold_storage);
                    notificationManager.notify(sosStatusNotificationId, notificationBuilder.build());

                     if (ringtone.isPlaying()) {
                         ringtone.stop();
                     }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


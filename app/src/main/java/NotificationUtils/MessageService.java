package NotificationUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.financemanager.AuthActivity;
import com.example.financemanager.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import Utils.FirebaseUtil;

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUtil.currentUserDetails().child("token").setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification");
        Intent resultIntent = new Intent(this, AuthActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(Objects.requireNonNull(message.getNotification()).getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.baseline_notifications_24);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "notification";
        NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Transfers", NotificationManager.IMPORTANCE_HIGH
            );
        channel.canBubble();
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(CHANNEL_ID);

        notificationManager.notify(100, builder.build());
    }
}

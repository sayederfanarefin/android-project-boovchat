package info.sayederfanarefin.boovchatv2;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.net.MalformedURLException;

/**
 * Created by SayedErfan on 8/11/2015.
 */
public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
String registrationId;
    @Override
    public void onRegistered(Context context,  final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                try {
                    Chats.mClient.getPush().register(gcmRegistrationId, null);
                    registrationId = gcmRegistrationId;
                    return null;
                }
                catch(Exception e) {
                    // handle error
                }
                return null;
            }
        }.execute();

        MobileServiceClient client;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "none");
        try {
            client = new MobileServiceClient(
                    "https://boovchat.azure-mobile.net/",
                    "mlDOQRtfCWzENVjrROuNlehjtDCMse49", context);

            MobileServiceTable<Registration> registrationTable = client.getTable(Registration.class);
            Registration registration = new Registration();
            registration.setRegistrationId(registrationId);
            registration.setId(user_email);
            registrationTable.insert(registration, new TableOperationCallback<Registration>() {
                @Override
                public void onCompleted(Registration entity, Exception exception,
                                        ServiceFilterResponse response) {
                    if (exception != null) {

                    } else {

                    }
                }

            });
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");

        sendNotification(nhMessage);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, Chats.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Message!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

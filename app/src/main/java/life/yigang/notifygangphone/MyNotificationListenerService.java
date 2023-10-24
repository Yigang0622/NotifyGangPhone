package life.yigang.notifygangphone;

import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class MyNotificationListenerService extends NotificationListenerService {

    private BarkPushService barkPushService;

    @Override
    public void onCreate() {
        super.onCreate();
        barkPushService = new BarkPushService(getApplicationContext());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String text = "";
        if (sbn.getNotification().tickerText != null) {
            text = sbn.getNotification().tickerText.toString();
        }
        String packageName = sbn.getPackageName();
        long when = sbn.getNotification().when;

        MyNotification myNotification = new MyNotification();
        myNotification.setContent(text);
        myNotification.setAppPackageName(packageName);
        myNotification.setWhen(when);

        barkPushService.makeNewNotificationPushRequest(myNotification);

        Log.d("MyNotificationListenerService", sbn.toString());
    }


    private String getAppName(String packageName) {
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }



}

package life.yigang.notifygangphone;

import android.app.Notification;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyNotificationListenerService extends NotificationListenerService {

    private BarkPushService barkPushService;

    private final List<String> titleKeys = new ArrayList<>();
    private final List<String> contentKeys = new ArrayList<>();

    private final Map<String, String> packageNameAppNameMap = new HashMap<>();

    public MyNotificationListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        barkPushService = new BarkPushService(getApplicationContext());
        titleKeys.add(Notification.EXTRA_TITLE);
        titleKeys.add(Notification.EXTRA_TITLE_BIG);

        contentKeys.add(Notification.EXTRA_BIG_TEXT);
        contentKeys.add(Notification.EXTRA_TEXT);
        contentKeys.add(Notification.EXTRA_SUMMARY_TEXT);
        contentKeys.add(Notification.EXTRA_SUB_TEXT);
        contentKeys.add(Notification.EXTRA_INFO_TEXT);

        packageNameAppNameMap.put("com.tencent.mm", "微信");
        packageNameAppNameMap.put("com.android.settings", "设置");
        packageNameAppNameMap.put("com.homelink.android", "链家");
        packageNameAppNameMap.put("com.lianjia.beike", "贝壳");

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String appName = getAppName(sbn.getPackageName());
        long when = sbn.getNotification().when;

        Bundle bundle = sbn.getNotification().extras;
        String title = getFirstString(bundle, titleKeys, "");
        String content = getFirstString(bundle, contentKeys, "");
        String combinedTitle = String.format("%s:%s",appName , title);

        MyNotification myNotification = new MyNotification();
        myNotification.setAppPackageName(combinedTitle);
        myNotification.setContent(content);
        myNotification.setWhen(when);

        barkPushService.makeNewNotificationPushRequest(myNotification);

        Log.d("MyNotificationListenerService", sbn.toString());
    }

    public String getAppName(String packageName) {
        if (this.packageNameAppNameMap.containsKey(packageName)) {
            return packageNameAppNameMap.get(packageName);
        } else {
            return packageName;
        }
    }

    public String getFirstString(Bundle bundle, List<String> keys, String defaultValue) {
        for (String key: keys) {
            String result = bundle.getString(key);
            if (result != null) {
                return result;
            }
        }
        return defaultValue;
    }

}

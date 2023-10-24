package life.yigang.notifygangphone;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button saveButton;
    private Button sendTestNotificationButton;

    private EditText barkKeyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveButton = findViewById(R.id.save_button);
        sendTestNotificationButton = findViewById(R.id.button_send);
        barkKeyEditText = findViewById(R.id.edit_text_bark_key);


        saveButton.setOnClickListener(view -> {
            String barkKey = barkKeyEditText.getText().toString();
            saveBarkKey(barkKey);
            loadBarkKey();
            Toast.makeText(this, "saved: " + MyEnv.barkKey, Toast.LENGTH_SHORT).show();
        });


        sendTestNotificationButton.setOnClickListener(view -> {
            sendTestNotification();
        });

        loadBarkKey();
        checkAndEnableNotificationPermission();
    }

    private void loadBarkKey() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String barkKey = sharedPref.getString(getString(R.string.bark_key_pref), "");
        MyEnv.barkKey = barkKey;
        barkKeyEditText.setText(barkKey);
    }

    private void saveBarkKey(String barkKey) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.bark_key_pref), barkKey);
        editor.apply();
    }

    private void sendTestNotification() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("testMessages", "Test", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "testMessages")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("NotifyGangPhone")
                .setContentText("Hello World")
                .setTicker("Hello World")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkAndEnableNotificationPermission() {
        boolean notificationListenerEnabled = isNotificationListenerEnabled(this);
        Log.i("MainActivity", "notificationListenerEnabled: " + notificationListenerEnabled);
        if (!notificationListenerEnabled) {
            openNotificationListenSettings();
        }
    }

    private String getAppName(String packageName) {
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
            e.printStackTrace();
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }


    public void openNotificationListenSettings() {
        try {
            Intent intent;
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }



}
package life.yigang.notifygangphone;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class BarkPushService implements Response.Listener<String>, Response.ErrorListener{

//    https://api.day.app/barkKey/推送标题/这里改成你自己的推送内容
    private final RequestQueue requestQueue;
    public BarkPushService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void makeNewNotificationPushRequest(MyNotification notification) {
        String title = notification.getAppPackageName();
        String content = notification.getContent();
        String requestUrl = String.format("https://api.day.app/%s/%s/%s",MyEnv.barkKey, title, content);
        StringRequest stringRequest = new StringRequest(requestUrl, this, this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("BarkPushService", error.toString());
    }

    @Override
    public void onResponse(String response) {
        Log.i("BarkPushService", response);
    }
}

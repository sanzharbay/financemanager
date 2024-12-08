package NotificationUtils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that sends push notification
 */
public class FCMNotificationSender {
    private final String userFCMToken;
    private final String title;
    private final String body;
    private final Context context;
    private final JSONObject data;
    private final String postURL = "https://fcm.googleapis.com/v1/projects/finance-manager-sanzhar/messages:send";

    public FCMNotificationSender(String userFCMToken, String title, String body, Context context, JSONObject data) {
        this.userFCMToken = userFCMToken;
        this.title = title;
        this.body = body;
        this.context = context;
        this.data = data;
    }

    public void sendNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();

            notificationObject.put("title", title);
            notificationObject.put("body", body);


            messageObject.put("token", userFCMToken);
            messageObject.put("notification", notificationObject);
            messageObject.put("data", data);

            mainObj.put("message", messageObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, mainObj, response -> {

            }, volleyError -> {

            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders(){
                    AccessToken accessToken = new AccessToken();
                    String token = accessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "Bearer " + token);
                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

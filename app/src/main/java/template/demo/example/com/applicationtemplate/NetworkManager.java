package template.demo.example.com.applicationtemplate;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Justin on 2015/10/25.
 */
public class NetworkManager {

    private static NetworkManager sInstance;

    private RequestQueue mQueue;

    private NetworkManager(Context context) {
        mQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManager(context);
        }
        return sInstance;
    }

    public void request(String tag, Request<?> request) {
        if (tag != null) {
            request.setTag(tag);
        }
        mQueue.add(request);
    }

    public void cancelRequest(String tag) {
        if (tag != null) {
            mQueue.cancelAll(tag);
        }
    }

    public void stop() {
        mQueue.stop();
    }
}

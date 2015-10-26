package template.demo.example.com.applicationtemplate;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mListView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        retrieveData(0, true);
    }

    private void retrieveData(int page, boolean isReload) {
        if (isReload) {
            mProgressDialog = ProgressDialog.show(this, "", "Please wait", true, false);
        }
        String api = getString(R.string.api);
        StringBuilder builder = new StringBuilder(api);
        builder.append("&");
        builder.append(getString(R.string.parameters));
        builder.append("&");
        builder.append(getString(R.string.paging, page));
        Log.d(TAG, "api " + builder.toString());

        StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), mOnCompleteListener, mOnErrorListener);
        NetworkManager.getInstance(this).request(null, request);
    }

    private Response.Listener<String> mOnCompleteListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject json = new JSONObject(s);
                JSONObject responseData = json.optJSONObject("responseData");
                if (responseData != null) {
                    JSONArray array = responseData.optJSONArray("results");
                    int size = array.length();
                    ArrayList<Model> list = new ArrayList<Model>();
                    for (int i = 0; i < size; i++) {
                        JSONObject jsonObj = array.optJSONObject(i);
                        Model model = new Model();
                        model.title = jsonObj.optString("titleNoFormatting");
                        model.description = jsonObj.optString("contentNoFormatting");
                        model.imageURL = jsonObj.optString("url");

                        list.add(model);
                    }

                    ListViewAdapter adapter = new ListViewAdapter(list);
                    mListView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        }
    };

    private Response.ErrorListener mOnErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };

    private class ListViewAdapter extends BaseAdapter {

        private ArrayList<Model> mData;

        ListViewAdapter(ArrayList<Model> data) {
            mData = (ArrayList<Model>) data.clone();
        }

        @Override
        public int getCount() {
            return (mData != null) ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return (mData != null) ? mData.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

            Model data = (Model) getItem(position);
            title.setText(data.title);
            desc.setText(data.description);

            int photoSize = parent.getContext().getResources().getDimensionPixelSize(R.dimen.icon_size);

            Picasso.with(parent.getContext()).load(data.imageURL).placeholder(R.drawable.ic_default).resize(photoSize, photoSize).centerCrop().into(icon);

            return convertView;
        }
    }
}

package com.example.template;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        String api = null;
        try {
            api = URLDecoder.decode(getString(R.string.api), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "api " + api);

        StringRequest request = new StringRequest(Request.Method.GET, api, mOnCompleteListener, mOnErrorListener);
        NetworkManager.getInstance(this).request(null, request);
    }

    private Object findInstance(String fieldName, Object baseObject) {
        if (TextUtils.isEmpty(fieldName)) {
            return null;
        }
        ArrayList<String> fieldNameList = new ArrayList<String>();
        String[] fieldSplitted = fieldName.split("\\.");
        if (fieldSplitted != null && fieldSplitted.length > 0) {
            for (String split : fieldSplitted) {
                int lodashIndex = split.indexOf("_");
                if (lodashIndex > 0) {
                    String preFix = split.substring(0, lodashIndex);
                    String postFix = split.substring(lodashIndex + 1, split.length());
                    char[] stringArray = postFix.trim().toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    postFix = new String(stringArray);
                    split = preFix + postFix;
                }
                fieldNameList.add(split);
            }
        } else {
            fieldNameList.add(fieldName);
        }

        Iterator<String> itr = fieldNameList.iterator();
        Object instance = null;
        try {
            while (itr.hasNext()) {
                String field = itr.next();
                if (instance == null) {
                    Field f = baseObject.getClass().getField(field);
                    instance = f.get(baseObject);
                } else {
                    Field f = instance.getClass().getField(field);
                    instance = f.get(instance);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Response.Listener<String> mOnCompleteListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                ArrayList<Model> list = new ArrayList<Model>();
                Gson gson = new Gson();
                Sample test = gson.fromJson(s, Sample.class);

                String dataListFieldName = getString(R.string.field_data_list);
                if (TextUtils.isEmpty(dataListFieldName)) {
                    Log.e(TAG, "not specified data field");
                    return;
                }

                Object instance = findInstance(dataListFieldName, test);
                if (instance == null) {
                    Log.e(TAG, "can not instance data field ");
                    return;
                }

                List<Object> resultList = (List<Object>) instance;

                String titleFieldName = getString(R.string.field_title);
                String descFieldName = getString(R.string.field_desc);
                String iconFieldName = getString(R.string.field_icon);

                for (Object result : resultList) {
                    Model model = new Model();
                    Object titleInstance = findInstance(titleFieldName, result);
                    if (titleInstance != null) {
                        if (titleInstance instanceof List) {
                            model.title = ((List) titleInstance).get(0).toString();
                        } else {
                            model.title = titleInstance.toString();
                        }
                    }

                    Object descInstance = findInstance(descFieldName, result);
                    if (descInstance != null) {
                        if (descInstance instanceof List) {
                            model.description = ((List) descInstance).get(0).toString();
                        } else {
                            model.description = descInstance.toString();
                        }
                    }

                    Object iconInstance = findInstance(iconFieldName, result);
                    if (iconInstance != null) {
                        if (iconInstance instanceof List) {
                            model.imageURL = ((List) iconInstance).get(0).toString();
                        } else {
                            model.imageURL = iconInstance.toString();
                        }
                    }
                    list.add(model);
                }
                ListViewAdapter adapter = new ListViewAdapter(list);
                mListView.setAdapter(adapter);
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
            Toast.makeText(MainActivity.this, "Error happened!", Toast.LENGTH_LONG).show();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
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

            if (!TextUtils.isEmpty(data.imageURL)) {
                Picasso.with(parent.getContext()).load(data.imageURL).placeholder(R.drawable.ic_default).resize(photoSize, photoSize).centerCrop().into(icon);
            }

            return convertView;
        }
    }
}

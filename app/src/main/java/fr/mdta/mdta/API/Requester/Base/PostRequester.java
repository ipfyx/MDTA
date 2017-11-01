package com.sli.app.API.Requester.Base;

/**
 * Created by baptiste on 19/08/17.
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

import com.sli.app.API.APItools;
import com.sli.app.API.Callback.Callback;
import com.sli.app.AppModel.User;
import com.sli.app.MyCustomProgressDialog;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class PostRequester extends AsyncTask<String, String, String> {

    protected HttpURLConnection mUrlConnection;
    protected Context mContext;
    protected MyCustomProgressDialog mDialog;
    protected String mPostData;
    protected Callback mCallback;
    protected URL mUrl;
    protected boolean isWithAuthentication;
    protected boolean isJSON;
    protected boolean isWithProgressDialog;

    protected PostRequester(String url, Context context, boolean withProgressDialog, boolean withAuthentication, Callback callback, boolean isJSON, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mUrl = new URL(url + getUrlArgs(strings));
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new MyCustomProgressDialog(mContext);
        }
        this.mPostData = getBody(strings);
        this.isWithAuthentication = withAuthentication;
        this.isJSON = isJSON;
    }

    protected PostRequester(String url, Context context, boolean withProgressDialog, boolean withAuthentication, Callback callback, boolean isJSON, Object object, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mUrl = new URL(url + getUrlArgs(strings));
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new MyCustomProgressDialog(mContext);
        }
        this.mPostData = getBodyFromObject(object);
        this.isWithAuthentication = withAuthentication;
        this.isJSON = isJSON;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    abstract protected String getUrlArgs(String... strings);

    abstract protected String getBody(String... strings);

    protected String getBodyFromObject(Object object) {
        return APItools.convertObjectToJSONString(object);
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = null;

        try {

            // Create the mUrlConnection
            mUrlConnection = (HttpURLConnection) mUrl.openConnection();

            mUrlConnection.setDoInput(true);
            mUrlConnection.setDoOutput(true);
            if (this.isJSON) {
                mUrlConnection.setRequestProperty("Content-Type", "application/json");
            } else {
                mUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }

            if (isWithAuthentication) {
                mUrlConnection.setRequestProperty("Authorization", "Bearer " + User.getInstance(mContext).getmToken().getmAccessToken());
            }

            mUrlConnection.setRequestMethod("POST");

            // Send the post body
            if (this.mPostData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(mUrlConnection.getOutputStream());
                writer.write(this.mPostData);
                writer.flush();
                Log.d("postdata", mPostData);
            }

            int statusCode = mUrlConnection.getResponseCode();
            Log.d("statuscode", statusCode + "");
            if (statusCode == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = new BufferedInputStream(mUrlConnection.getInputStream());

                response = convertStreamToString(inputStream);
                return response;

            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isWithProgressDialog) {
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.getWindow().setGravity(Gravity.CENTER);
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (isWithProgressDialog) {
            mDialog.dismiss();
        }
    }
}

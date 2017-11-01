package com.sli.app.API.Requester.Base;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;

import com.sli.app.API.Callback.Callback;
import com.sli.app.AppModel.User;
import com.sli.app.MyCustomProgressDialog;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by baptiste on 20/08/17.
 */

public abstract class GetRequester extends AsyncTask<String, String, String> {

    protected HttpURLConnection mUrlConnection;
    protected Context mContext;
    protected MyCustomProgressDialog mDialog;
    protected Callback mCallback;
    protected URL mUrl;
    protected boolean isWithAuthentication;
    protected boolean isWithProgressDialog;

    protected GetRequester(String url, Context context, boolean withProgressDialog, boolean withAuthentication, Callback callback, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new MyCustomProgressDialog(mContext);
        }
        this.mUrl = new URL(url + getUrlArgs(strings));

        this.isWithAuthentication = withAuthentication;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    abstract protected String getUrlArgs(String... strings);


    @Override
    protected String doInBackground(String... strings) {
        String response;

        try {
            // Create the mUrlConnection

            mUrlConnection = (HttpURLConnection) mUrl.openConnection();

            mUrlConnection.setDoInput(true);

            mUrlConnection.setRequestProperty("Content-Type", "text/html");
            if (isWithAuthentication) {
                mUrlConnection.setRequestProperty("Authorization", "Bearer " + User.getInstance(mContext).getmToken().getmAccessToken());
            }
            mUrlConnection.setRequestMethod("GET");
            int statusCode = mUrlConnection.getResponseCode();
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

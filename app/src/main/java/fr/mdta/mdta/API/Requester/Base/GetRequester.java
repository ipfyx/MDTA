package fr.mdta.mdta.API.Requester.Base;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.mdta.mdta.API.Callback.Callback;


public abstract class GetRequester extends AsyncTask<String, String, String> {

    protected HttpURLConnection mUrlConnection;
    protected Context mContext;
    protected ProgressDialog mDialog;
    protected Callback mCallback;
    protected URL mUrl;
    protected boolean isWithProgressDialog;

    protected GetRequester(String url, Context context, boolean withProgressDialog, Callback callback, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new ProgressDialog(mContext);
        }
        this.mUrl = new URL(url + getUrlArgs(strings));
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
            mUrlConnection = (HttpURLConnection) mUrl.openConnection();
            mUrlConnection.setDoInput(true);
            mUrlConnection.setRequestProperty("Content-Type", "text/html");
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

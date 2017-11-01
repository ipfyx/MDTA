package fr.mdta.mdta.API.Requester.Base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;

public abstract class PostRequester extends AsyncTask<String, String, String> {

    protected HttpURLConnection mUrlConnection;
    protected Context mContext;
    protected ProgressDialog mDialog;
    protected String mPostData;
    protected Callback mCallback;
    protected URL mUrl;
    protected boolean isJSON;
    protected boolean isWithProgressDialog;

    protected PostRequester(String url, Context context, boolean withProgressDialog, Callback callback, boolean isJSON, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mUrl = new URL(url + getUrlArgs(strings));
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new ProgressDialog(mContext);
        }
        this.mPostData = getBody(strings);
        this.isJSON = isJSON;
    }

    protected PostRequester(String url, Context context, boolean withProgressDialog, Callback callback, boolean isJSON, Object object, String... strings) throws MalformedURLException {
        this.mContext = context;
        this.mUrl = new URL(url + getUrlArgs(strings));
        this.mCallback = callback;
        this.isWithProgressDialog = withProgressDialog;
        if (isWithProgressDialog) {
            this.mDialog = new ProgressDialog(mContext);
        }
        this.mPostData = getBodyFromObject(object);
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
        String response;

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

            mUrlConnection.setRequestMethod("POST");

            // Send the post body
            if (this.mPostData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(mUrlConnection.getOutputStream());
                writer.write(this.mPostData);
                writer.flush();
            }

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

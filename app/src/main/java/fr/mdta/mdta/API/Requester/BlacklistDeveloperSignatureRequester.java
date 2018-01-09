package fr.mdta.mdta.API.Requester;


import android.content.Context;
import android.util.Log;

import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.SentItem.DeveloperSignature;
import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.Base.PostRequester;

public class BlacklistDeveloperSignatureRequester extends PostRequester {


    public BlacklistDeveloperSignatureRequester(Context context, boolean withProgressDialog, Callback callback,
                                                DeveloperSignature developerSignature) throws MalformedURLException {
        super(APItools.URL_API_BLACKLIST, context, withProgressDialog, callback, true, developerSignature);
    }

    @Override
    protected String getUrlArgs(String... strings) {
        return "";
    }

    @Override
    protected String getBody(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (response == null) {
            mCallback.OnErrorHappended();
            return;
        }
        Log.d("result", response);


        mCallback.OnTaskCompleted(response);

    }

}

package fr.mdta.mdta.API.Requester;


import android.content.Context;
import android.util.Log;

import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.ReceivedItem.BasicScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.Base.PostRequester;

public class BasicScanRequester extends PostRequester {


    public BasicScanRequester(Context context, boolean withProgressDialog, Callback callback,
                              PackagesList packagesList) throws MalformedURLException {
        super(APItools.URL_API_BASIC_SCAN, context, withProgressDialog, callback, true, packagesList);
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
        BasicScanResultItem itemResponse = APItools.convertJSONToObject(response, BasicScanResultItem.class);


        mCallback.OnTaskCompleted(itemResponse);
    }

}

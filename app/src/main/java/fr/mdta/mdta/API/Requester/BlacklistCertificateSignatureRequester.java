package fr.mdta.mdta.API.Requester;


import android.util.Log;

import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.SentItem.CertificateSignature;
import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.Base.PostRequester;

public class BlacklistCertificateSignatureRequester extends PostRequester {


    public BlacklistCertificateSignatureRequester(Callback callback,
                                                  CertificateSignature certificateSignature) throws MalformedURLException {
        super(APItools.URL_API_BLACKLIST_CERTIFICATE, callback, true, certificateSignature);
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

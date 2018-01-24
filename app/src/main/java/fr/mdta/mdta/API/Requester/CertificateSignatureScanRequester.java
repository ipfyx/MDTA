package fr.mdta.mdta.API.Requester;


import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.ReceivedItem.CertificateSignatureScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.CertificateSignatureList;
import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.Base.PostRequester;

public class CertificateSignatureScanRequester extends PostRequester {


    public CertificateSignatureScanRequester(Callback callback,
                                             CertificateSignatureList certificateSignatureList) throws MalformedURLException {
        super(APItools.URL_API_CERTIFICATE_SIGNATURE_SCAN, callback, true, certificateSignatureList);
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
        CertificateSignatureScanResultItem itemResponse = APItools.convertJSONToObject(response, CertificateSignatureScanResultItem.class);


        mCallback.OnTaskCompleted(itemResponse.getResult());
    }

}

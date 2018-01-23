package fr.mdta.mdta.API.Requester;


import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.ReceivedItem.DeveloperSignatureScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.DeveloperSignatureList;
import fr.mdta.mdta.API.APItools;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.Base.PostRequester;

public class DeveloperSignatureScanRequester extends PostRequester {


    public DeveloperSignatureScanRequester(Callback callback,
                                           DeveloperSignatureList developerSignatureList) throws MalformedURLException {
        super(APItools.URL_API_DEVELOPER_SIGNATURE_SCAN, callback, true, developerSignatureList);
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
        DeveloperSignatureScanResultItem itemResponse = APItools.convertJSONToObject(response, DeveloperSignatureScanResultItem.class);


        mCallback.OnTaskCompleted(itemResponse.getResult());
    }

}

package fr.mdta.mdta.API.APIModel.SentItem;

import java.util.ArrayList;

public class DeveloperSignatureList {
    private ArrayList<DeveloperSignatureListElement> DeveloperSignatures;

    public class DeveloperSignatureListElement {
        private String PackageName;
        private String KeyAlgorithm;
        private String KeyBase64;
    }
}

package fr.mdta.mdta.API.APIModel.SentItem;

import java.util.ArrayList;

public class DeveloperSignatureList {
    private ArrayList<DeveloperSignatureListElement> DeveloperSignatures;

    public DeveloperSignatureList(ArrayList<DeveloperSignatureListElement> developerSignatures) {
        DeveloperSignatures = developerSignatures;
    }

    public static class DeveloperSignatureListElement {
        private String PackageName;
        private String KeyAlgorithm;
        private String KeyBase64;

        public DeveloperSignatureListElement(String packageName, String keyAlgorithm, String keyBase64) {
            PackageName = packageName;
            KeyAlgorithm = keyAlgorithm;
            KeyBase64 = keyBase64;
        }
    }
}

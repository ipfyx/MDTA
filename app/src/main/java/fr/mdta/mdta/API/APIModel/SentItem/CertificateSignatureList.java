package fr.mdta.mdta.API.APIModel.SentItem;

import java.util.ArrayList;

public class CertificateSignatureList {
    private ArrayList<CertificateSignatureListElement> CertificateSignatures;

    public CertificateSignatureList(ArrayList<CertificateSignatureListElement> developerSignatures) {
        CertificateSignatures = developerSignatures;
    }

    public static class CertificateSignatureListElement {
        private String PackageName;
        private String SigAlgorithm;
        private String SigBase64;

        public CertificateSignatureListElement(String packageName, String sigAlgorithm, String sigBase64) {
            PackageName = packageName;
            SigAlgorithm = sigAlgorithm;
            SigBase64 = sigBase64;
        }
    }
}

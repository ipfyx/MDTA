package fr.mdta.mdta.API.APIModel.SentItem;


public class CertificateSignature {

    private String package_name;
    private String sig_algorithm;
    private String sig_base64;

    public CertificateSignature(String package_name, String sig_algorithm, String sig_base64) {
        this.package_name = package_name;
        this.sig_algorithm = sig_algorithm;
        this.sig_base64 = sig_base64;
    }
}

package fr.mdta.mdta.API.APIModel.SentItem;


public class DeveloperSignature {

    private String package_name;
    private String key_algorithm;
    private String key_base64;

    public DeveloperSignature(String package_name, String key_algorithm, String key_base64) {
        this.package_name = package_name;
        this.key_algorithm = key_algorithm;
        this.key_base64 = key_base64;
    }
}

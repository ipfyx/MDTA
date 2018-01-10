package fr.mdta.mdta.SignaturesScanner;

import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

/**
 * Created by manwefm on 10/01/18.
 */

public class CertRSA extends Signature {

    private final byte[] mSignature;
    private int mHashCode;
    private boolean mHaveHashCode;
    private SoftReference<String> mStringRef;
    private Certificate[] mCertificateChain;

    public CertRSA(byte[] signature) {
        super(signature);
        mSignature = signature.clone();
        mCertificateChain = null;
    }

    /**
     * Returns the public key for this signature.
     *
     * @throws CertificateException when Signature isn't a valid X.509
     *             certificate; shouldn't happen.
     * @hide
     */
    public PublicKey getPublicKey() throws CertificateException {
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        final ByteArrayInputStream bais = new ByteArrayInputStream(mSignature);
        final Certificate cert = certFactory.generateCertificate(bais);
        return cert.getPublicKey();
    }

    /**
     * Used for compatibility code that needs to check the certificate chain
     * during upgrades.
     *
     * @throws CertificateEncodingException
     * @hide
     */
    public Signature[] getChainSignatures() throws CertificateEncodingException {
        if (mCertificateChain == null) {
            return new Signature[] { this };
        }

        Signature[] chain = new Signature[1 + mCertificateChain.length];
        chain[0] = this;

        int i = 1;
        for (Certificate c : mCertificateChain) {
            chain[i++] = new Signature(c.getEncoded());
        }

        return chain;
    }

}

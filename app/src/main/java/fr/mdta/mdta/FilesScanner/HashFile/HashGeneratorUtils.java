package fr.mdta.mdta.FilesScanner.HashFile;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash functions utility class.
 *
 * @author www.codejava.net
 *         http://www.codejava.net/coding/how-to-calculate-md5-and-sha-hash-values-in-java
 */
public class HashGeneratorUtils {

    public static String generateMD5(String message) throws HashGenerationException {
        return hashString(message, "MD5");
    }

    public static String generateSHA1(String message) throws HashGenerationException {
        return hashString(message, "SHA-1");
    }

    public static String generateSHA256(String message) throws HashGenerationException {
        return hashString(message, "SHA-256");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String generateMD5(File file) throws HashGenerationException {
        return hashFile(file, "MD5");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String generateSHA1(File file) throws HashGenerationException {
        return hashFile(file, "SHA-1");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String generateSHA256(File file) throws HashGenerationException {
        return hashFile(file, "SHA-256");
    }

    private static String hashString(String message, String algorithm)
            throws HashGenerationException {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new HashGenerationException(
                    "Could not generate hash from String", ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String hashFile(File file, String algorithm)
            throws HashGenerationException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new HashGenerationException(
                    "Could not generate hash from file", ex);
        }
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}

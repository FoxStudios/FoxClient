package net.foxes4life.foxclient.networking;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BrokenHash {
    public static String hash(String str) {
        try {
            byte[] digest = digest(str, "SHA-1");
            return new BigInteger(digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] digest(String str, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        return md.digest(strBytes);
    }
}
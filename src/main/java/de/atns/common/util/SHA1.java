package de.atns.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
// -------------------------- STATIC METHODS --------------------------

    public static String createSHA1Code(final String text) {
        return digest(text, "SHA-1", 16);
    }

    public static String digest(final String text, final String algorithm, final int radix) {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(text.getBytes(), 0, text.length());
            return new BigInteger(1, md.digest()).toString(radix);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("NoSuchAlgorithmException: %s", e));
        }
    }
}

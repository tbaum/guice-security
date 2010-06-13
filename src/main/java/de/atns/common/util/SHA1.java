package de.atns.common.util;

import java.security.*;

public class SHA1 {
// -------------------------- STATIC METHODS --------------------------

    public static String createSHA1Code(final String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes(), 0, text.length());
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("NoSuchAlgorithmException: %s", e));
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}

package de.atns.common.security;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class SecurityTokenServiceImpl implements SecurityTokenService {

    private final long maxAge;
    private final String secretMagic;

    public SecurityTokenServiceImpl(long maxAge, String secretMagic) {
        this.maxAge = maxAge;
        this.secretMagic = secretMagic;
    }

    public byte[] createDigest(String text) {
        final MessageDigest md = getDigest();
        md.update(text.getBytes(), 0, text.length());
        return md.digest();
    }

    protected MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(format("NoSuchAlgorithmException: %s", e));
        }
    }

    public String validateToken(String token) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(parseBase64Binary(token)));
            byte[] sha = new byte[20];
            if (dis.read(sha) != 20) {
                throw new IllegalArgumentException("token is invalid");
            }
            long timestamp = dis.readLong();
            String email = dis.readUTF();

            if (!Arrays.equals(hashup(timestamp, email), sha)) {
                throw new IllegalArgumentException("token is invalid");
            }
            if (isExpired(timestamp)) {
                throw new IllegalArgumentException("token has expired");
            }

            return email;
        } catch (IOException e) {
            throw new IllegalArgumentException("token is invalid");
        }
    }

    protected boolean isExpired(long timestamp) {
        return now() - timestamp > maxAge * 1000;
    }

    protected long now() {
        return currentTimeMillis();
    }

    private byte[] hashup(long timestamp, String email) {
        return createDigest(timestamp + secretMagic + email);
    }

    public String createToken(String login) {
        try {
            Long timestamp = now();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
            stream.write(hashup(timestamp, login));
            stream.writeLong(timestamp);
            stream.writeUTF(login);
            stream.close();

            return printBase64Binary(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalArgumentException("token is invalid");
        }
    }
}

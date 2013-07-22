package com.google.inject.extensions.security;

import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class SecurityTokenServiceImpl implements SecurityTokenService {

    private final int complexity;
    private final long maxAge;
    private final byte[] salt;

    /**
     * @param maxAge     time in seconds a token is allowed to age until getting invalid
     * @param salt       base64-encoded 16-bytes long salt, use {@link SecurityTokenServiceImpl.createSalt()} to create
     * @param complexity number of bcrypt-rounds, good value is 8 to 12
     */

    public SecurityTokenServiceImpl(long maxAge, String salt, int complexity) {
        this.maxAge = maxAge;
        this.salt = BCrypt.decode_base64(salt, 16);
        this.complexity = complexity;
    }

    public static String createSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return BCrypt.encode_base64(salt, 16);
    }

    /**
     * @param token, generated from {@link createToken()}
     * @return the login part of the token if valid
     * @throws IllegalArgumentException if token has expired or is invalid
     */
    public String validateToken(String token) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(parseBase64Binary(token)));
            byte[] sha = new byte[24];
            if (dis.read(sha) != 24) {
                throw new IllegalArgumentException("token is invalid");
            }
            long timestamp = dis.readLong();
            String email = dis.readUTF();

            if (isExpired(timestamp)) {
                throw new IllegalArgumentException("token has expired");
            }

            if (!Arrays.equals(hashup(timestamp, email), sha)) {
                throw new IllegalArgumentException("token is invalid");
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

    private byte[] hashup(long timestamp, String email) throws UnsupportedEncodingException {
        return new BCrypt().crypt_raw((timestamp + email).getBytes("UTF-8"), salt, complexity);
    }

    /**
     * create a new token based on current time and passed login parameter
     *
     * @param login
     * @return a base64 string containing, 24 byte[] bcrypt signature, 1 Long timestamp, login-string
     */
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

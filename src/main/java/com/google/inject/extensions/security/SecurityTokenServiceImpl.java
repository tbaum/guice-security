package com.google.inject.extensions.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.slf4j.LoggerFactory.getLogger;

public class SecurityTokenServiceImpl implements SecurityTokenService {
    private static final Logger LOG = getLogger(SecurityTokenServiceImpl.class);
    private final Map<String, Token> userCache = new HashMap<>();
    private final Map<String, Token> tokenCache = new ConcurrentHashMap<>();
    private final long maxAge;
    private final int complexity;
    private final long regenerateAfter;
    private final byte[] salt;
    private long oldestToken = Long.MAX_VALUE;

    /**
     * @param regenerateAfter time in ms a cached token is reused
     * @param maxAge          time in ms a token is allowed to age until getting invalid
     * @param salt            base64-encoded 16-bytes salt, created with {@link SecurityTokenServiceImpl#createSalt()}
     * @param complexity      number of bcrypt-rounds, good value is 8 to 12
     */
    public SecurityTokenServiceImpl(long regenerateAfter, long maxAge, String salt, int complexity) {
        this.regenerateAfter = regenerateAfter;
        this.maxAge = maxAge;
        this.salt = BCrypt.decode_base64(salt, 16);
        this.complexity = complexity;
    }

    /**
     * @return a random 16-byte salt, base64-encoded
     */
    public static String createSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return BCrypt.encode_base64(salt, 16);
    }

    /**
     * @param token, generated from {@link SecurityTokenServiceImpl#createToken(String)}
     * @return the login part of the token if valid
     * @throws IllegalArgumentException if token has expired or is invalid
     */
    public String validateToken(String token) {

        // try to use cache
        cleanupTokenCache();
        Token cachedToken = tokenCache.get(token);
        if (cachedToken != null && !isExpired(cachedToken.valid)) {
            LOG.debug("found valid token in cached {}", cachedToken);
            return cachedToken.user;
        }

        // check token
        final byte[] sha = new byte[24];
        final long timestamp;
        final String user;
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(parseBase64Binary(token)));
            //noinspection ResultOfMethodCallIgnored
            dis.read(sha);
            timestamp = dis.readLong();
            user = dis.readUTF();
        } catch (IOException e) {
            throw new InvalidTokenException();
        }

        if (isExpired(timestamp)) {
            throw new TokenExpiredException();
        }

        if (!Arrays.equals(createHash(timestamp, user), sha)) {
            throw new InvalidTokenException();
        }

        addTokenToCache(user, timestamp, token);
        return user;
    }

    protected boolean isExpired(long timestamp) {
        return now() - timestamp > maxAge;
    }

    protected long now() {
        return currentTimeMillis();
    }

    private byte[] createHash(long timestamp, String user) {
        try {
            return new BCrypt().crypt_raw((timestamp + user).getBytes("UTF-8"), salt, complexity);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create a new token based on current time and passed user parameter
     *
     * @param user
     * @return a base64 string containing, 24 byte[] bcrypt signature, 1 Long timestamp, user-string
     */
    public String createToken(String user) {
        final long now = now();

        // try use cache
        cleanupTokenCache();
        Token cachedToken = userCache.get(user);

        if (cachedToken != null && now - cachedToken.valid < regenerateAfter) {
            LOG.debug("using cached token {}", cachedToken);
            return cachedToken.token;
        }

        // generate new token
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
            stream.write(createHash(now, user));
            stream.writeLong(now);
            stream.writeUTF(user);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String token = printBase64Binary(byteArrayOutputStream.toByteArray());
        addTokenToCache(user, now, token);
        return token;
    }

    protected Map<String, Token> getUserCache() {
        return userCache;
    }

    protected Map<String, Token> getTokenCache() {
        return tokenCache;
    }

    private synchronized void cleanupTokenCache() {
        if (!isExpired(oldestToken)) {
            return;
        }

        LOG.debug("check for expired tokens");
        oldestToken = Long.MAX_VALUE;
        for (Token token : new ArrayList<>(userCache.values())) {
            if (isExpired(token.valid)) {
                LOG.debug("remove expired token from cache {}", token);
                userCache.remove(token.user);
                tokenCache.remove(token.token);
                continue;
            }
            oldestToken = Math.min(oldestToken, token.valid);
        }
    }

    private synchronized void addTokenToCache(String user, long timestamp, String token) {
        Token newToken = new Token(token, user, timestamp);

        Token existingToken = userCache.get(newToken.user);
        if (existingToken == null || existingToken.valid < timestamp) {
            LOG.debug("add token to cache {}", newToken);

            if (existingToken != null) {
                tokenCache.remove(existingToken.token);
            }
            userCache.put(newToken.user, newToken);
            tokenCache.put(newToken.token, newToken);
            oldestToken = Math.min(oldestToken, newToken.valid);
        } else {
            LOG.debug("not adding older token to cache {}", newToken);
        }
    }

    static class Token {
        final String user;
        final String token;
        final long valid;

        private Token(String token, String user, long valid) {
            this.token = token;
            this.user = user;
            this.valid = valid;
        }

        @Override public String toString() {
            return "Token{user='" + user + "' token='" + token + "', valid=" + valid + "}";
        }
    }
}

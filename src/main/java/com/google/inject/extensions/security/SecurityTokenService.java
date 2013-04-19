package com.google.inject.extensions.security;

public interface SecurityTokenService {

    String validateToken(String token);

    String createToken(String login);

}

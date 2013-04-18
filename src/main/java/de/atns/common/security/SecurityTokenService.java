package de.atns.common.security;

public interface SecurityTokenService {

    String validateToken(String token);

    String createToken(String login);

}

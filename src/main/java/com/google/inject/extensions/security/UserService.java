package com.google.inject.extensions.security;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface UserService {

    SecurityUser findUser(String login);

    SecurityUser findUser(String login, String password);

}

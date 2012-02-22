package de.atns.common.security;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface UserService {

    SecurityUser findUser(String login, String pass);

    SecurityUser refreshUser(String login);

    void setActive(SecurityUser user);

    void setInactive(SecurityUser user);

    void successfullLogin(SecurityUser user);
}

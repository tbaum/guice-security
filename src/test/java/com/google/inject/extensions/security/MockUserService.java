package com.google.inject.extensions.security;

/**
 * @author tbaum
 * @since 23.12.2013
 */
class MockUserService implements UserService {
    @Override public SecurityUser findUser(String login) {
        return null;
    }

    @Override public SecurityUser findUser(String login, String password) {
        return null;
    }
}

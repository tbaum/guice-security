package com.google.inject.extensions.security.jersey;

import com.google.inject.extensions.security.SecurityRole;
import com.google.inject.extensions.security.SecurityUser;
import com.google.inject.extensions.security.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author tbaum
 * @since 28.04.2014
 */
public class UserServiceImpl implements UserService {

    public Map<String, SecurityUser> users = new HashMap<String, SecurityUser>() {{
        put("remote1", new SecurityUserImpl("remote1", "abcdef"));
        put("remote2", new SecurityUserImpl("remote2", "abcdef"));
    }};

    @Override public SecurityUser findUser(String login) {
        return users.get(login);
    }

    @Override public SecurityUser findUser(final String login, String password) {
        SecurityUserImpl user = (SecurityUserImpl) findUser(login);
        return user.password.equals(password) ? user : null;
    }

    public interface SpecialRole extends SecurityRole {

    }

    private static class SecurityUserImpl implements SecurityUser {
        private final String login;
        private final String password;
        //  private String token;

        public SecurityUserImpl(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override public String getUsername() {
            return login;
        }

        @Override public Set<Class<? extends SecurityRole>> getRoles() {
            HashSet<Class<? extends SecurityRole>> classes = new HashSet<>();
            classes.add(SpecialRole.class);
            return classes;
        }


        //   @Override public String getToken() {
        //       return token;
        //   }

        //  @Override public void setToken(String token) {
        //      this.token = token;
        //  }
    }
}

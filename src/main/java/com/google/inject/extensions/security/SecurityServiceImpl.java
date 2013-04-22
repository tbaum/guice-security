package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@Singleton public class SecurityServiceImpl implements SecurityService {

    private final SecurityScope securityScope;
    private final UserService userService;
    private final SecurityTokenService securityTokenService;

    @Inject public SecurityServiceImpl(UserService userService, SecurityScope securityScope,
                                       SecurityTokenService securityTokenService) {
        this.userService = userService;
        this.securityScope = securityScope;
        this.securityTokenService = securityTokenService;
    }

    @Override public SecurityUser currentUser() {
        return securityScope.get();
    }

    @Override public String authenticate(String token) {
        String login = securityTokenService.validateToken(token);
        return authenticate(userService.findUser(login));
    }

    @Override public String authenticate(SecurityUser user) {
        if (user == null) return null;

        String token = securityTokenService.createToken(user.getLogin());
        user.setToken(token);

        securityScope.put(user);
        return token;
    }

    @Override public SecurityUser clearAuthentication() {
        SecurityUser user = currentUser();
        if (user != null) {
            user.setToken(null);
        }
        securityScope.put(null);

        return user;
    }
}

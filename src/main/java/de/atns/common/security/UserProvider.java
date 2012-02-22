package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class UserProvider implements Provider<SecurityUser> {

    private final SecurityScope securityScope;
    private final UserService userService;

    @Inject public UserProvider(final SecurityScope securityScope, final UserService userService) {
        this.securityScope = securityScope;
        this.userService = userService;
    }

    @Override public SecurityUser get() {
        final SecurityUser id = securityScope.get(SecurityUser.class);
        if (id == null) {
            return null;
        }
        return userService.refreshUser(id.getLogin());
    }
}

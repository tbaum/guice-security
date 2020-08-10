package com.google.inject.extensions.security;

import com.google.inject.ImplementedBy;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@ImplementedBy(SecurityServiceImpl.class)
public interface SecurityService {

    SecurityUser currentUser();

//    String authenticate(String securityToken);

    void authenticate(SecurityUser securityUser);

//    SecurityUser clearAuthentication();
}

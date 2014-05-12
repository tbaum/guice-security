package com.google.inject.extensions.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public interface AuthFilterPlugin {

    boolean authenticate(HttpServletRequest request, HttpServletResponse response);

    void postAuth(HttpServletRequest request, HttpServletResponse response);

}

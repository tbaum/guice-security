package com.google.inject.extensions.security.jersey;

import com.google.inject.Injector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.google.inject.extensions.security.SecurityFilter.INJECTOR;

/**
 * @author tbaum
 * @since 28.04.2014
 */
public class TestContextListener implements ServletContextListener {
    static Injector injector;

    protected Injector createInjector() {
        return injector;
    }

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(INJECTOR, createInjector());
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}

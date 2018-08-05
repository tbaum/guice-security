package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.GuiceBerryEnvMain;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.extensions.security.SecurityFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Inject;

import static org.apache.catalina.startup.Tomcat.addServlet;
import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

/**
 * @author tbaum
 * @since 19.03.2014
 */
public class TomcatGuiceBerryEnvMain extends AbstractModule implements GuiceBerryEnvMain {

    public static int SERVER_PORT = 0;
    public static String BASE_URI = null;
    @Inject
    private Injector injector;
    @Inject
    private Tomcat tomcat;

    @Override
    public void run() {
        Context context = tomcat.addContext("", null);
        Wrapper wrapper = addServlet(context, TestApplication.class.getName(), ServletContainer.class.getName());
        wrapper.addInitParameter(JAXRS_APPLICATION_CLASS, TestApplication.class.getName());
        wrapper.setAsyncSupported(true);
        wrapper.setLoadOnStartup(1);

        context.addServletMappingDecoded("/api/*", wrapper.getName());
        context.addApplicationListener(TestContextListener.class.getName());

        TestContextListener.injector = injector;

        context.addFilterDef(new FilterDef() {{
            setFilterName("SecurityFilter");
            setFilter(injector.getInstance(SecurityFilter.class));
            setAsyncSupported("true");
        }});

        context.addFilterMap(new FilterMap() {{
            addURLPattern("/*");
            setFilterName("SecurityFilter");
        }});
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        Connector connector = tomcat.getConnector();
        SERVER_PORT = connector.getLocalPort();
        BASE_URI = "http://localhost:" + SERVER_PORT;
    }

    @Override
    protected void configure() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(SERVER_PORT);
        tomcat.setBaseDir("target/tomcat");
        tomcat.setHostname("localhost");
        bind(Tomcat.class).toInstance(tomcat);
        bind(GuiceBerryEnvMain.class).to(TomcatGuiceBerryEnvMain.class);
    }
}

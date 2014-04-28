package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.GuiceBerryEnvMain;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.extensions.security.SecurityFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.Tomcat;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

import static org.apache.catalina.startup.Tomcat.addServlet;

/**
 * @author tbaum
 * @since 19.03.2014
 */
public class TomcatGuiceBerryEnvMain extends AbstractModule implements GuiceBerryEnvMain {

    public static final int SERVER_PORT = 18456;
    public static final String BASE_URI = "http://localhost:" + SERVER_PORT;
    @Inject Injector injector;
    @Inject Tomcat tomcat;

    @Override public void run() {
        try {
            File tempFile = File.createTempFile("tomcat-test", "tmp");
            tempFile.delete();
            tempFile.mkdirs();
            String baseDir = tempFile.getCanonicalPath();

            Context rootCtx = tomcat.addContext("", baseDir);

            Wrapper wrapper = addServlet(rootCtx, TestApplication.class.getName(),
                    "org.glassfish.jersey.servlet.ServletContainer");
            wrapper.addInitParameter("javax.ws.rs.Application", TestApplication.class.getName());
            wrapper.setAsyncSupported(true);
            wrapper.setLoadOnStartup(1);

            rootCtx.addServletMapping("/api/*", wrapper.getName());
            rootCtx.addApplicationListener(TestContextListener.class.getName());

            TestContextListener.injector = injector;

            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName("SecurityFilter");
            filterDef.setFilter(injector.getInstance(SecurityFilter.class));
            filterDef.setAsyncSupported("true");
            rootCtx.addFilterDef(filterDef);

            FilterMap filterMap = new FilterMap();
            filterMap.addURLPattern("/*");
            filterMap.setFilterName("SecurityFilter");
            rootCtx.addFilterMap(filterMap);


            tomcat.start();
        } catch (IOException | LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override protected void configure() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(SERVER_PORT);
        bind(Tomcat.class).toInstance(tomcat);
        bind(GuiceBerryEnvMain.class).to(TomcatGuiceBerryEnvMain.class);
    }
}

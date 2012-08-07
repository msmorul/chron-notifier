/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.regex.Pattern;

/**
 * Web application lifecycle listener.
 * @author toaster
 */
public class ManifestDirectoryListener implements ServletContextListener {

    /**
     * manifest directory setting from webapp context. used to store ingested manifests
     */
    public static final String MANIFEST_DIRECTORY = "manifestDirectory";
    public static final String VALID_REGEX = "pathregex";
    private static File directory;
    private static Pattern pathRegex;

    public static Pattern getPathRegex() {
        return pathRegex;
    }

    public static File getDirectory() {
        return directory;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (sce.getServletContext().getInitParameter(MANIFEST_DIRECTORY) != null) {
            directory = new File(sce.getServletContext().getInitParameter(MANIFEST_DIRECTORY));
            if (!directory.isDirectory()) {
                if (!directory.mkdirs()) {
                    throw new RuntimeException("Cannot create directory: " + directory.getAbsolutePath());
                }
            }
        } else {
            throw new RuntimeException("No manifest directory configured in contect");
        }


        if (sce.getServletContext().getInitParameter(VALID_REGEX) != null) {
            pathRegex = Pattern.compile(sce.getServletContext().getInitParameter(VALID_REGEX));
        } else {
            pathRegex = Pattern.compile(".*");
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}

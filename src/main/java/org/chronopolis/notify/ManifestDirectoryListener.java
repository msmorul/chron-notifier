/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

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
    private static final Logger LOG = Logger.getLogger(ManifestDirectoryListener.class);

    /**
     * Return pattern which will match allowed path characters. Configurable
     * via pathregex in the webapps's context. Default is to allow all char's
     * 
     * @return pattern of allowable characters
     */
    public static Pattern getPathRegex() {
        return pathRegex;
    }

    /**
     * 
     * @return manifest storage directory
     */
    public static File getDirectory() {
        return directory;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // Load manifest directory
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
        LOG.info("Using manifest directory: " + directory.getAbsolutePath());

        // Load path regex
        if (sce.getServletContext().getInitParameter(VALID_REGEX) != null) {
            pathRegex = Pattern.compile(sce.getServletContext().getInitParameter(VALID_REGEX));
        } else {
            pathRegex = Pattern.compile(".*");
        }
        LOG.info("Loaded path filter pattern: " + pathRegex.pattern());

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}

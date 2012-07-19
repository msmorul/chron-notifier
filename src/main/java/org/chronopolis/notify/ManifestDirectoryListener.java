/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chronopolis.notify;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 * @author toaster
 */
public class ManifestDirectoryListener implements ServletContextListener {

    /**
     * manifest directory setting from webapp context. used to store ingested manifests
     */
    public static final String MANIFEST_DIRECTORY = "manifestDirectory";
    private static File directory;

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

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}

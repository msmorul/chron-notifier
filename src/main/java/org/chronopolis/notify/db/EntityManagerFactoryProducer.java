/*
 * Copyright 2012 toaster.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.chronopolis.notify.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author toaster
 */
public class EntityManagerFactoryProducer implements ServletContextListener {

    private static EntityManagerFactory emf;

    public synchronized static EntityManagerFactory get() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("notification-PU");
        }
        return emf;

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public synchronized void contextDestroyed(ServletContextEvent sce) {
        if (emf != null) {
            emf.close();
        }
    }
}

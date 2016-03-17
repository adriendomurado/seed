/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.application;

import com.google.inject.Injector;
import org.apache.commons.configuration.Configuration;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;

import javax.inject.Inject;
import java.io.File;

/**
 * Implementation of the {@link Application} interface.
 *
 * @author adrien.lauer@mpsa.com
 */
class ApplicationImpl implements Application {
    private final ApplicationInfo applicationInfo;
    private final File storageRoot;
    private final ConfigurationProvider configurationProvider;

    @Inject
    private Injector injector;

    public ApplicationImpl(ApplicationInfo applicationInfo, ConfigurationProvider configurationProvider, File seedStorage) {
        this.applicationInfo = applicationInfo;
        this.configurationProvider = configurationProvider;
        this.storageRoot = seedStorage;
    }

    @Override
    public String getName() {
        return applicationInfo.getName();
    }

    @Override
    public String getId() {
        return applicationInfo.getId();
    }

    @Override
    public String getVersion() {
        return applicationInfo.getVersion();
    }

    @Override
    public File getStorageLocation(String context) {
        if (storageRoot == null) {
            throw SeedException.createNew(ApplicationErrorCode.NO_LOCAL_STORAGE_CONFIGURED).put("context", context);
        }

        File location = new File(storageRoot, context);

        if (!location.exists() && !location.mkdirs()) {
            throw SeedException.createNew(ApplicationErrorCode.UNABLE_TO_CREATE_STORAGE_DIRECTORY).put("path", location.getAbsolutePath());
        }

        if (!location.isDirectory()) {
            throw SeedException.createNew(ApplicationErrorCode.STORAGE_PATH_IS_NOT_A_DIRECTORY).put("path", location.getAbsolutePath());
        }

        if (!location.canWrite()) {
            throw SeedException.createNew(ApplicationErrorCode.STORAGE_DIRECTORY_IS_NOT_WRITABLE).put("path", location.getAbsolutePath());
        }

        return location;
    }

    @Override
    public String getInjectionGraph(String filter) {
        throw new UnsupportedOperationException("Injection graph dump is no longer supported in core");
    }

    @Override
    public String getInjectionGraph() {
        return getInjectionGraph(null);
    }

    @Override
    public Configuration getConfiguration() {
        return configurationProvider.getConfiguration();
    }

    @Override
    public Configuration getConfiguration(Class<?> clazz) {
        return configurationProvider.getConfiguration(clazz);
    }

    @Override
    public String substituteWithConfiguration(String value) {
        return configurationProvider.substituteWithConfiguration(value);
    }
}

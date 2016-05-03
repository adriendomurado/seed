/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.application;

import com.google.inject.AbstractModule;
import org.seedstack.seed.Application;

/**
 * Guice module that bind the {@link Application} interface and bind the configuration type listener.
 *
 * @author adrien.lauer@mpsa.com
 */
class ApplicationModule extends AbstractModule {
    private final Application application;

    ApplicationModule(Application application) {
        this.application = application;
    }

    @Override
    protected void configure() {
        bind(Application.class).toInstance(this.application);
    }

}

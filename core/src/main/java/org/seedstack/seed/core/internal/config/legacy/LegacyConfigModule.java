/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.config.legacy;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.apache.commons.configuration.Configuration;

public class LegacyConfigModule extends AbstractModule {
    private final Configuration configuration;

    public LegacyConfigModule(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new ConfigurationTypeListener(configuration));
    }
}

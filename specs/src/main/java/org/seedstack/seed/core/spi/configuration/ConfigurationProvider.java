/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.spi.configuration;

import io.nuun.kernel.api.annotations.Facet;
import org.apache.commons.configuration.Configuration;

import java.util.Map;

/**
 * This interface provides methods to access the application configuration.
 * <p>
 * It is exposed by the ApplicationPlugin to other plugins. It cannot be injected.
 * </p>
 *
 * @deprecated
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@Facet
@Deprecated
public interface ConfigurationProvider {
    /**
     * Return the default configuration.
     *
     * @deprecated
     * @return the default configuration.
     */
    @Deprecated
    Map<String, String> getDefaultConfiguration();

    /**
     * Return the application global configuration.
     *
     * @deprecated
     * @return the configuration object.
     */
    @Deprecated
    Configuration getConfiguration();

    /**
     * Looks for eventual props configuration for a class.
     *
     * @deprecated
     * @return the configuration map
     */
    @Deprecated
    Configuration getConfiguration(Class<?> clazz);

    /**
     * Substitute any ${...} expression in the given string with the configuration values.
     *
     * @deprecated
     * @param value the string to substitute.
     * @return the substituted string.
     */
    @Deprecated
    String substituteWithConfiguration(String value);
}

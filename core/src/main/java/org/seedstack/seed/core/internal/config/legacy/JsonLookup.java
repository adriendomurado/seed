/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.config.legacy;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.seedstack.seed.spi.configuration.ConfigurationLookup;

import java.util.regex.Pattern;

/**
 * This class resolve json interpolations.
 *
 * @author adrien.lauer@mpsa.com
 */
@ConfigurationLookup("json")
public class JsonLookup extends StrLookup {
    public static final Pattern LOOKUP_PATTERN = Pattern.compile("\\|");

    private final ConfigurationInterpolator interpolator;
    private final StrSubstitutor substitutor;

    /**
     * Creates the lookup.
     *
     * @param configuration the legacy configuration.
     */
    public JsonLookup(Configuration configuration) {
        this.interpolator = ((AbstractConfiguration) configuration).getInterpolator();
        this.substitutor = ((AbstractConfiguration) configuration).getSubstitutor();
    }

    @Override
    public String lookup(String s) {
        String[] split = LOOKUP_PATTERN.split(s);
        if (split.length != 2) {
            throw new IllegalArgumentException("Json lookup must follow this syntax: ${json:propertyContainingJson|jsonPath}");
        }

        String jsonPath = substitutor.replace(split[1]);
        if (!jsonPath.startsWith("$")) {
            jsonPath = "$." + jsonPath;
        }

        return JsonPath.parse(substitutor.replace(interpolator.lookup(split[0]))).read(jsonPath).toString();
    }
}

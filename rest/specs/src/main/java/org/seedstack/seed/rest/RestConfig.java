/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.rest;

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Config("rest")
public class RestConfig {
    @SingleValue
    private String path = "";
    private String jspPath = "/WEB-INF/jsp";
    private String baseRel = "";
    private String baseParam = "";
    private boolean jsonHome = true;
    private Map<String, String> jerseyProperties = new HashMap<>();
    private Set<Class<?>> features = new HashSet<>();
    private ExceptionMappingConfig exceptionMapping = new ExceptionMappingConfig();

    public String getPath() {
        return path;
    }

    public String getJspPath() {
        return jspPath;
    }

    public String getBaseRel() {
        return baseRel;
    }

    public String getBaseParam() {
        return baseParam;
    }

    public boolean isJsonHome() {
        return jsonHome;
    }

    public Map<String, String> getJerseyProperties() {
        return Collections.unmodifiableMap(jerseyProperties);
    }

    public Set<Class<?>> getFeatures() {
        return Collections.unmodifiableSet(features);
    }

    public ExceptionMappingConfig exceptionMapping() {
        return exceptionMapping;
    }

    @Config("exceptionMapping")
    public static class ExceptionMappingConfig {
        private boolean security = true;
        private boolean all = true;

        public boolean isSecurity() {
            return security;
        }

        public boolean isAll() {
            return all;
        }
    }
}

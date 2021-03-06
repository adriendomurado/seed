/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.web;

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Config("web")
public class WebConfig {
    private boolean requestDiagnostic;
    private StaticResourcesConfig staticResources = new StaticResourcesConfig();
    private CORSConfig cors = new CORSConfig();
    private ServerConfig serverConfig = new ServerConfig();

    public boolean isRequestDiagnosticEnabled() {
        return requestDiagnostic;
    }

    public StaticResourcesConfig staticResources() {
        return staticResources;
    }

    public CORSConfig cors() {
        return cors;
    }

    public ServerConfig serverConfig() {
        return serverConfig;
    }

    @Config("cors")
    public static class CORSConfig {
        @SingleValue
        private boolean enabled;
        private String path = "/*";
        private Map<String, String> properties = new HashMap<>();

        public boolean isEnabled() {
            return enabled;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getProperties() {
            return properties;
        }
    }

    @Config("static")
    public static class StaticResourcesConfig {
        private static final int DEFAULT_BUFFER_SIZE = 65535;

        @SingleValue
        private boolean enabled = true;
        private int bufferSize = DEFAULT_BUFFER_SIZE;
        private boolean minification = true;
        private boolean gzip = true;
        private boolean gzipOnTheFly = true;
        private CacheConfig cache = new CacheConfig();

        public boolean isEnabled() {
            return enabled;
        }

        public int getBufferSize() {
            return bufferSize;
        }

        public boolean isMinificationEnabled() {
            return minification;
        }

        public boolean isGzipEnabled() {
            return gzip;
        }

        public boolean isOnTheFlyGzipEnabled() {
            return gzipOnTheFly;
        }

        public CacheConfig cacheConfig() {
            return cache;
        }

        @Config("cache")
        public static class CacheConfig {
            private static final int DEFAULT_CACHE_MAX_SIZE = 8192;
            private static final int DEFAULT_CACHE_CONCURRENCY = 32;

            private int maxSize = DEFAULT_CACHE_MAX_SIZE;
            private int initialSize = maxSize / 4;
            private int concurrencyLevel = DEFAULT_CACHE_CONCURRENCY;

            public int getInitialSize() {
                return initialSize;
            }

            public int getMaxSize() {
                return maxSize;
            }

            public int getConcurrencyLevel() {
                return concurrencyLevel;
            }
        }
    }

    @Config("server")
    public static class ServerConfig {
        private static final String DEFAULT_HOST = "0.0.0.0";
        private static final int DEFAULT_PORT = 8080;
        private static final String DEFAULT_CONTEXT_PATH = "/";
        private static final boolean DEFAULT_HTTPS_ACTIVATION = false;
        private static final boolean DEFAULT_HTTP2_ACTIVATION = false;

        private String host = DEFAULT_HOST;
        @SingleValue
        private int port = DEFAULT_PORT;
        private String contextPath = DEFAULT_CONTEXT_PATH;
        private boolean https = DEFAULT_HTTPS_ACTIVATION;
        private boolean http2 = DEFAULT_HTTP2_ACTIVATION;

        public String getHost() {
            return host;
        }

        public ServerConfig setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public ServerConfig setPort(int port) {
            this.port = port;
            return this;
        }

        public String getContextPath() {
            return contextPath;
        }

        public ServerConfig setContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public boolean isHttps() {
            return https;
        }

        public ServerConfig setHttps(boolean https) {
            this.https = https;
            return this;
        }

        public boolean isHttp2() {
            return http2;
        }

        public ServerConfig setHttp2(boolean http2) {
            this.http2 = http2;
            return this;
        }
    }

    @Config("security")
    public static class SecurityConfig {
        private List<UrlConfig> urls = new ArrayList<>();
        private XSRFConfig xsrf = new XSRFConfig();

        public List<UrlConfig> getUrls() {
            return Collections.unmodifiableList(urls);
        }

        public SecurityConfig addUrl(UrlConfig urlConfig) {
            urls.add(urlConfig);
            return this;
        }

        public XSRFConfig xsrf() {
            return xsrf;
        }

        public static class UrlConfig {
            private String pattern = "/**";
            private List<String> filters = new ArrayList<>();

            public String getPattern() {
                return pattern;
            }

            public UrlConfig setPattern(String pattern) {
                this.pattern = pattern;
                return this;
            }

            public List<String> getFilters() {
                return Collections.unmodifiableList(filters);
            }

            public UrlConfig addFilters(String... filters) {
                this.filters.addAll(Arrays.asList(filters));
                return this;
            }
        }

        @Config("xsrf")
        public static class XSRFConfig {
            private String cookieName = "XSRF-TOKEN";
            private String headerName = "X-XSRF-TOKEN";
            private String algorithm = "SHA1PRNG";
            private int length = 32;

            public String getCookieName() {
                return cookieName;
            }

            public XSRFConfig setCookieName(String cookieName) {
                this.cookieName = cookieName;
                return this;
            }

            public String getHeaderName() {
                return headerName;
            }

            public XSRFConfig setHeaderName(String headerName) {
                this.headerName = headerName;
                return this;
            }

            public String getAlgorithm() {
                return algorithm;
            }

            public XSRFConfig setAlgorithm(String algorithm) {
                this.algorithm = algorithm;
                return this;
            }

            public int getLength() {
                return length;
            }

            public XSRFConfig setLength(int length) {
                this.length = length;
                return this;
            }
        }
    }
}

/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.config.legacy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import io.nuun.kernel.core.AbstractPlugin;
import jodd.props.Props;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.javatuples.Pair;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.CorePlugin;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;
import org.seedstack.seed.spi.configuration.ConfigurationLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LegacyConfigPlugin extends AbstractPlugin implements ConfigurationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyConfigPlugin.class);

    public static final String REGEX_FOR_SUBPACKAGE = "(.*)\\.([^.]*)$";
    public static final String BASE_PACKAGES_KEY = "org.seedstack.seed.base-packages";
    public static final String CONFIGURATION_PACKAGE = "META-INF.configuration";
    public static final String CONFIGURATION_LOCATION = "META-INF/configuration/";
    public static final String PROPS_REGEX = ".*\\.props";
    public static final String PROPERTIES_REGEX = ".*\\.properties";

    private final LegacyConfigLoader legacyConfigLoader = new LegacyConfigLoader();
    private final Configuration bootstrapConfiguration = legacyConfigLoader.buildBootstrapConfig();
    private final Map<String, String> defaultConfiguration = new ConcurrentHashMap<>();
    private final LegacyConfigDiagnosticCollector legacyConfigDiagnosticCollector = new LegacyConfigDiagnosticCollector();

    private Props props;
    private MapConfiguration configuration;

    @Override
    public String name() {
        return "legacy-config";
    }

    @Override
    public Collection<Class<?>> requiredPlugins() {
        return Lists.<Class<?>>newArrayList(CorePlugin.class);
    }

    @Override
    public String pluginPackageRoot() {
        String packageRoots = CONFIGURATION_PACKAGE;

        String[] applicationPackageRoots = bootstrapConfiguration.getStringArray(BASE_PACKAGES_KEY);
        if (applicationPackageRoots != null && applicationPackageRoots.length > 0) {
            packageRoots += "," + StringUtils.join(applicationPackageRoots, ",");
        }

        return packageRoots;
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .resourcesRegex(PROPERTIES_REGEX)
                .resourcesRegex(PROPS_REGEX)
                .annotationType(ConfigurationLookup.class)
                .build();
    }

    @Override
    public InitState init(InitContext initContext) {
        configuration = buildConfiguration(retrieveConfigurationResources(initContext));
        registerConfigurationLookups(configuration, findConfigurationLookups(initContext));

        initDiagnosticCollector(initContext);

        return InitState.INITIALIZED;
    }

    private void initDiagnosticCollector(InitContext initContext) {
        legacyConfigDiagnosticCollector.setBasePackages(pluginPackageRoot());

        String[] profiles = legacyConfigLoader.applicationProfiles();
        if (profiles == null || profiles.length == 0) {
            LOGGER.info("No configuration profile selected");
            legacyConfigDiagnosticCollector.setActiveProfiles("");
        } else {
            String activeProfiles = Arrays.toString(profiles);
            LOGGER.info("Active configuration profile(s): {}", activeProfiles);
            legacyConfigDiagnosticCollector.setActiveProfiles(activeProfiles);
        }

        legacyConfigDiagnosticCollector.setConfiguration(configuration);

        initContext.dependency(CorePlugin.class).registerDiagnosticCollector("org.seedstack.seed.core.legacy-config", legacyConfigDiagnosticCollector);
    }


    private Map<String, Class<? extends StrLookup>> findConfigurationLookups(InitContext initContext) {
        Map<String, Class<? extends StrLookup>> configurationLookups = new HashMap<String, Class<? extends StrLookup>>();

        for (Class<?> candidate : initContext.scannedClassesByAnnotationClass().get(ConfigurationLookup.class)) {
            ConfigurationLookup configurationLookup = candidate.getAnnotation(ConfigurationLookup.class);
            if (StrLookup.class.isAssignableFrom(candidate) && configurationLookup != null && !configurationLookup.value().isEmpty()) {
                configurationLookups.put(configurationLookup.value(), candidate.asSubclass(StrLookup.class));
                LOGGER.trace("Detected configuration lookup {}", configurationLookup.value());
            }
        }

        return configurationLookups;
    }

    private Set<String> retrieveConfigurationResources(InitContext initContext) {
        Set<String> allConfigurationResources = Sets.newHashSet();
        allConfigurationResources.addAll(collectConfigResources(initContext, PROPERTIES_REGEX));
        allConfigurationResources.addAll(collectConfigResources(initContext, PROPS_REGEX));
        return allConfigurationResources;
    }

    private List<String> collectConfigResources(InitContext initContext, String regex) {
        return initContext.mapResourcesByRegex()
                .get(regex)
                .stream()
                .filter(propsResource -> propsResource.startsWith(CONFIGURATION_LOCATION))
                .collect(Collectors.toList());
    }

    private MapConfiguration buildConfiguration(Set<String> allConfigurationResources) {
        Pair<MapConfiguration, Props> confs = legacyConfigLoader.buildConfig(allConfigurationResources, defaultConfiguration);
        MapConfiguration configuration = confs.getValue0();
        props = confs.getValue1();
        // TODO applicationDiagnosticCollector.setConfiguration(configuration);
        return configuration;
    }

    private void registerConfigurationLookups(MapConfiguration configuration, Map<String, Class<? extends StrLookup>> configurationLookups) {
        for (Map.Entry<String, Class<? extends StrLookup>> configurationLookup : configurationLookups.entrySet()) {
            configuration.getInterpolator().registerLookup(configurationLookup.getKey(), buildStrLookup(configurationLookup.getValue(), configuration));
        }
    }

    private StrLookup buildStrLookup(Class<? extends StrLookup> strLookupClass, Configuration configuration) {
        try {
            try {
                return strLookupClass.getConstructor(Configuration.class).newInstance(configuration);
            } catch (NoSuchMethodException e1) {
                try {
                    return strLookupClass.getConstructor().newInstance();
                } catch (NoSuchMethodException e2) {
                    throw SeedException.wrap(e2, LegacyConfigErrorCode.NO_SUITABLE_CONFIGURATION_LOOKUP_CONSTRUCTOR_FOUND).put("className", strLookupClass.getCanonicalName());
                }
            }
        } catch (Exception e) {
            throw SeedException.wrap(e, LegacyConfigErrorCode.UNABLE_TO_INSTANTIATE_CONFIGURATION_LOOKUP).put("className", strLookupClass.getCanonicalName());
        }
    }

    @Override
    public Object nativeUnitModule() {
        return new LegacyConfigModule(configuration);
    }

    /**
     * Return the internal props configuration.
     *
     * @return the internal props configuration.
     */
    public Props getProps() {
        return this.props;
    }

    @Override
    public Map<String, String> getDefaultConfiguration() {
        return defaultConfiguration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }


    @Override
    public Configuration getConfiguration(Class<?> clazz) {
        return new MapConfiguration(ImmutableMap.<String, Object>copyOf(getEntityConfiguration(clazz.getName())));
    }

    /**
     * Merge property from props section recursively starting by "*" section.
     *
     * @param key props section name
     * @return configuration map
     */
    private Map<String, String> getEntityConfiguration(String key) {
        Configuration configuration = this.configuration.subset("*");
        Map<String, String> entityConfig = new HashMap<>();
        entityConfig.putAll(configToMap(configuration));
        mergeEntityPackageConfiguration(key, entityConfig);
        return entityConfig;
    }

    /**
     * Convert a commons configuration object to a map.
     *
     * @param configuration The configuration to convert.
     * @return the map.
     */
    private Map<String, String> configToMap(Configuration configuration) {
        Map<String, String> entityConfiguration = new HashMap<>();
        configuration.getKeys().forEachRemaining(key -> entityConfiguration.put(key, configuration.getString(key)));
        return entityConfiguration;
    }

    /**
     * Merge property from props section recursively starting by the atomic
     * parent package section coming from entity class name. Properties can be
     * overwritten by using the same key on the subpackage(s) section.
     *
     * @param key props section name
     */
    private void mergeEntityPackageConfiguration(String key, Map<String, String> entityConfiguration) {
        if (key.matches(REGEX_FOR_SUBPACKAGE)) {
            mergeEntityPackageConfiguration(
                    key.replaceFirst(REGEX_FOR_SUBPACKAGE, "$1*"),
                    entityConfiguration);
        }
        Configuration configuration = this.configuration.subset(key.replace("*", ".*"));
        entityConfiguration.putAll(configToMap(configuration));
    }

    @Override
    public String substituteWithConfiguration(String value) {
        return configuration.getSubstitutor().replace(value);
    }
}

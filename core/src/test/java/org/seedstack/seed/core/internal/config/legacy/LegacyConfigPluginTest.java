/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.seed.core.internal.config.legacy;

import io.nuun.kernel.api.plugin.context.InitContext;
import org.apache.commons.configuration.Configuration;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.core.internal.CorePlugin;
import org.seedstack.seed.core.internal.init.SeedConfigLoader;
import org.seedstack.seed.spi.configuration.ConfigurationLookup;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ApplicationPlugin Test
 *
 * @author redouane.loulou@ext.mpsa.com
 */
public class LegacyConfigPluginTest {

    LegacyConfigPlugin pluginUnderTest;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        pluginUnderTest = new LegacyConfigPlugin();
    }

    @Test
    public void package_root_should_valid() {
        String pluginPackageRoot = pluginUnderTest.pluginPackageRoot();
        Assertions.assertThat(pluginPackageRoot).contains(LegacyConfigPlugin.CONFIGURATION_PACKAGE);
        Assertions.assertThat(pluginPackageRoot).contains("some.other.pkg");
    }

    @Test
    public void initTest() {
        Collection<String> propertiesFiles = new ArrayList<String>();
        propertiesFiles.add("META-INF/configuration/org.seedstack.seed-test.props");
        Map<String, Collection<String>> mapFile = new HashMap<String, Collection<String>>();
        mapFile.put(".*\\.props", propertiesFiles);
        InitContext initContext = mockInitContextForCore();
        pluginUnderTest.init(initContext);
        Configuration configuration = pluginUnderTest.getConfiguration();
        Assertions.assertThat(configuration).isNotNull();
        Assertions.assertThat(pluginUnderTest.nativeUnitModule()).isInstanceOf(LegacyConfigModule.class);
    }

    @Test
    public void initTest2() {
        InitContext initContext = mockInitContextForCore();
        pluginUnderTest.init(initContext);
        Configuration configuration = pluginUnderTest.getConfiguration();
        Assertions.assertThat(configuration).isNotNull();
        Assertions.assertThat(pluginUnderTest.nativeUnitModule()).isInstanceOf(LegacyConfigModule.class);

    }

    @SuppressWarnings("unchecked")
    public InitContext mockInitContextForCore() {
        InitContext initContext = mock(InitContext.class);
        Map<String, Collection<String>> resources = new HashMap<String, Collection<String>>();

        when(initContext.dependency(CorePlugin.class)).thenReturn(mock(CorePlugin.class));

        Map<Class<? extends Annotation>, Collection<Class<?>>> scannedClassesByAnnotationClass = new HashMap<Class<? extends Annotation>, Collection<Class<?>>>();
        scannedClassesByAnnotationClass.put(ConfigurationLookup.class, new ArrayList<Class<?>>());

        Collection<String> props = new ArrayList<String>();
        props.add("META-INF/configuration/org.seedstack.seed-test.props");
        resources.put(LegacyConfigPlugin.PROPS_REGEX, props);

        Collection<String> properties = new ArrayList<String>();
        properties.add("META-INF/configuration/any.properties");
        resources.put(LegacyConfigPlugin.PROPERTIES_REGEX, properties);

        when(initContext.mapResourcesByRegex()).thenReturn(resources);
        when(initContext.scannedClassesByAnnotationClass()).thenReturn(scannedClassesByAnnotationClass);

        return initContext;
    }
}

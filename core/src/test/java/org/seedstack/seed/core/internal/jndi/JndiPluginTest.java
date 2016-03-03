/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.jndi;

import org.fest.reflect.reference.TypeRef;
import org.seedstack.seed.Application;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.apache.commons.configuration.Configuration;
import org.assertj.core.api.Assertions;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.core.internal.CorePlugin;
import org.seedstack.seed.core.internal.application.ApplicationPlugin;
import org.seedstack.seed.core.internal.config.legacy.LegacyConfigPlugin;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;

import javax.naming.Context;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JndiPluginTest {

	JndiPlugin pluginUnderTest;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        pluginUnderTest = new JndiPlugin();
    }

	@Test
	public void initTest() {
		pluginUnderTest.init(mockInitContextForJndiPlugin("test1"));
        Assertions.assertThat(pluginUnderTest.nativeUnitModule()).isInstanceOf(JndiModule.class);
        Map<String, Context> additionalJndiContexts = Reflection.field("additionalJndiContexts").ofType(new TypeRef<Map<String, Context>>() {}).in(pluginUnderTest).get();
        Context defaultJndiContext = Reflection.field("defaultJndiContext").ofType(Context.class).in(pluginUnderTest).get();
        Assertions.assertThat(additionalJndiContexts).isNotNull();
        Assertions.assertThat(additionalJndiContexts.containsKey("test1")).isNotNull();
        Assertions.assertThat(defaultJndiContext).isNotNull();
	}

	@Test(expected = RuntimeException.class)
	public void initTest2() {
		pluginUnderTest.init(mockInitContextForJndiPlugin("test3"));
	}

	@Test
	public void requiredPluginsTest() {
		Assertions.assertThat(pluginUnderTest.requiredPlugins()).contains(ConfigurationProvider.class);
	}

	@Test
	public void nameTest() {
		Assertions.assertThat(pluginUnderTest.name()).isNotNull();
	}

	private InitContext mockInitContextForJndiPlugin(String nameTolookup){
		InitContext initContext = mock(InitContext.class);
		Configuration configuration = mock(Configuration.class);
		when(configuration.subset(CorePlugin.CORE_PLUGIN_PREFIX)).thenReturn(configuration);
		when(configuration.getStringArray("additional-jndi-contexts")).thenReturn(new String[]{nameTolookup});
		when(configuration.getString("additional-jndi-context.test1")).thenReturn("/jndi-test1.properties");
		LegacyConfigPlugin legacyConfigPlugin = mock(LegacyConfigPlugin.class);
		when(initContext.dependency(ConfigurationProvider.class)).thenReturn(legacyConfigPlugin);
        when(legacyConfigPlugin.getConfiguration()).thenReturn(configuration);
		return initContext;
	}


}

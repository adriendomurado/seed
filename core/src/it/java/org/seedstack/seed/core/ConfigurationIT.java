/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core;

import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seedstack.coffig.Config;
import org.seedstack.coffig.ConfigurationException;
import org.seedstack.coffig.SingleValue;
import org.seedstack.seed.Application;
import org.seedstack.seed.ApplicationConfig;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.core.fixtures.SomeEnum;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationIT {
    @Rule
    public SeedITRule rule = new SeedITRule(this);
    private Injector injector;

    private static class Holder {
        @Inject
        Application application;

        @Configuration("secret1")
        String secret1;

        @Configuration(value = "dummy", defaultValue = "defaultValue")
        String dummy;

        @Configuration("someEnum")
        SomeEnum someEnum;

        @Configuration("anInt")
        int anInt;

        @Configuration("someShorts")
        short[] someShorts;

        @Configuration
        ConfigObject configObject1;

        @Configuration(value = "missingProperty", defaultValue = "5")
        ConfigObject configObject2;
    }

    @Before
    public void setUp() throws Exception {
        injector = rule.getKernel().objectGraph().as(Injector.class).createChildInjector((Module) binder -> binder.bind(Holder.class));
    }

    @Config("someObject")
    private static class ConfigObject {
        String property1;
        @SingleValue
        int[] property2;
    }

    @Test
    public void configuration_injection_is_working_correctly() {
        Holder holder = injector.getInstance(Holder.class);
        assertThat(holder).isNotNull();
        assertThat(holder.application).isNotNull();
        assertThat(holder.secret1).isNotNull().isEqualTo("**I am Alice**");
        assertThat(holder.dummy).isNotNull().isEqualTo("defaultValue");
        assertThat(holder.anInt).isNotNull().isEqualTo(5);
        assertThat(holder.someShorts).isNotEmpty().isEqualTo(new short[]{2, 3, 4});
        assertThat(holder.someEnum).isNotNull().isEqualTo(SomeEnum.FOO);
    }

    @Test
    public void configuration_can_be_retrieved() {
        Holder holder = injector.getInstance(Holder.class);
        assertThat(holder.application.getConfiguration().get(ApplicationConfig.class).getId()).isEqualTo("seed-it");
    }

    @Test
    public void system_properties_are_accessible_in_configuration() {
        Holder holder = injector.getInstance(Holder.class);
        assertThat(holder.application.getConfiguration().get(String.class, "sys.java\\.vendor")).isEqualTo(System.getProperty("java.vendor"));
    }

    @Test
    public void environment_variables_are_accessible_in_configuration() {
        Holder holder = injector.getInstance(Holder.class);
        String java_home = System.getenv("JAVA_HOME");
        if (java_home != null) {
            assertThat(holder.application.getConfiguration().get(String.class, "env.JAVA_HOME")).isEqualTo(java_home);
        }
    }

    @Test
    public void empty_configuration_values_yield_empty_string() {
        Holder holder = injector.getInstance(Holder.class);
        assertThat(holder.application.getConfiguration().getMandatory(String.class, "empty")).isEqualTo("");
    }

    @Test(expected = ConfigurationException.class)
    public void non_existent_configuration_values_throws_exception() {
        Holder holder = injector.getInstance(Holder.class);
        holder.application.getConfiguration().getMandatory(String.class, "nonExistent");
    }

    @Test
    public void configuration_object_injection() {
        Holder holder = injector.getInstance(Holder.class);
        assertThat(holder.configObject1).isNotNull();
        assertThat(holder.configObject1.property1).isEqualTo("value");
        assertThat(holder.configObject1.property2).containsExactly(5, 6, 7);
        assertThat(holder.configObject2).isNotNull();
        assertThat(holder.configObject2.property1).isNull();
        assertThat(holder.configObject2.property2).containsExactly(5);
    }
}

package org.seedstack.seed.core.internal.config.legacy;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.coffig.data.MapNode;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LegacyConfigurationProviderTest {
    Configuration testConfig;
    LegacyConfigurationProvider underTest;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> testConfigMap = new HashMap<>();
        testConfigMap.put("org.seedstack.seed.core.application-id", "test-app-id");
        testConfigMap.put("org.seedstack.test.values", "value1, value2, value3");
        testConfigMap.put("org.seedstack.jpa.units", "unit1, unit2");
        testConfigMap.put("org.seedstack.jpa.unit.unit1.url", "url1");
        testConfigMap.put("org.seedstack.jpa.unit.unit1.user", "user1");
        testConfigMap.put("org.seedstack.jpa.unit.unit2.url", "url2");
        testConfigMap.put("org.seedstack.jpa.unit.unit2.user", "user2");
        testConfigMap.put("custom.property.key", "test-custom-property-value");
        testConfig = new MapConfiguration(testConfigMap);
        underTest = new LegacyConfigurationProvider(testConfig);
    }

    @Test
    public void provider_doesnt_return_null() throws Exception {
        assertThat(underTest.provide()).isNotNull();
    }

    @Test
    public void simple_key_is_correctly_mapped() throws Exception {
        assertThat(underTest.provide().search("custom.property.key").value()).isEqualTo("test-custom-property-value");
    }

    @Test
    public void seed_key_is_stripped() throws Exception {
        assertThat(underTest.provide().search("core.application-id").value()).isEqualTo("test-app-id");
    }

    @Test
    public void multi_value_key_is_correctly_handled() throws Exception {
        assertThat(underTest.provide().search("test.values").value()).isEqualTo("value1,value2,value3");
    }

    @Test
    public void multi_key_conversion_is_correctly_handled() throws Exception {
        MapNode provide = underTest.provide();
        assertThat(provide.search("jpa.units.unit1.url").value()).isEqualTo("url1");
        assertThat(provide.search("jpa.units.unit1.user").value()).isEqualTo("user1");
        assertThat(provide.search("jpa.units.unit2.url").value()).isEqualTo("url2");
        assertThat(provide.search("jpa.units.unit2.user").value()).isEqualTo("user2");
    }
}

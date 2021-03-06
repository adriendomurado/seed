/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.configuration;

import com.google.common.base.Joiner;
import com.google.inject.MembersInjector;
import org.seedstack.coffig.Coffig;
import org.seedstack.coffig.node.ArrayNode;
import org.seedstack.coffig.node.ValueNode;
import org.seedstack.seed.Configuration;
import org.seedstack.shed.exception.SeedException;
import org.seedstack.seed.core.internal.CoreErrorCode;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

/**
 * Guice members injector that inject logger instances.
 *
 * @param <T> The type of class to inject.
 * @author adrien.lauer@mpsa.com
 */
class ConfigurationMembersInjector<T> implements MembersInjector<T> {
    private final Set<ConfigurableField> fields;
    private final Coffig coffig;

    ConfigurationMembersInjector(Coffig coffig, Set<ConfigurableField> fields) {
        this.coffig = coffig;
        this.fields = fields;
    }

    @Override
    public void injectMembers(T t) {
        for (ConfigurableField configurableField : fields) {
            Configuration configuration = configurableField.getConfiguration();
            Field field = configurableField.getField();
            Optional<?> optionalValue = coffig.getOptional(field.getType(), configuration.value());

            try {
                if (optionalValue.isPresent()) {
                    field.set(t, optionalValue.get());
                } else {
                    String[] defaultValue = configuration.defaultValue();
                    if (defaultValue.length > 0) {
                        field.set(t, mapValue(defaultValue, field.getType()));
                    } else if (configuration.mandatory()) {
                        throw SeedException.createNew(CoreErrorCode.MISSING_CONFIGURATION_KEY)
                                .put("key", Joiner.on(".").join(configuration.value()));
                    }
                }
            } catch (Exception e) {
                throw SeedException.wrap(e, CoreErrorCode.UNABLE_TO_INJECT_CONFIGURATION_VALUE)
                        .put("class", field.getDeclaringClass().getCanonicalName())
                        .put("field", field.getName())
                        .put("key", Joiner.on(".").join(configuration.value()));
            }
        }
    }

    private Object mapValue(String[] value, Class<?> type) {
        if (value.length > 1) {
            return coffig.getMapper().map(new ArrayNode(value), type);
        } else if (value.length == 1) {
            return coffig.getMapper().map(new ValueNode(value[0]), type);
        } else {
            return null;
        }
    }

    static class ConfigurableField {
        private final Field field;
        private final Configuration configuration;

        ConfigurableField(Field field, Configuration configuration) {
            this.field = field;
            this.configuration = configuration;
        }

        Field getField() {
            return field;
        }

        Configuration getConfiguration() {
            return configuration;
        }
    }
}
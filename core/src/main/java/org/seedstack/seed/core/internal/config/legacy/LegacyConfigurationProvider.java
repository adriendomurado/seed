/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.config.legacy;

import org.apache.commons.configuration.Configuration;
import org.seedstack.coffig.MapNode;
import org.seedstack.coffig.MutableMapNode;
import org.seedstack.coffig.MutableTreeNode;
import org.seedstack.coffig.TreeNode;
import org.seedstack.coffig.ValueNode;
import org.seedstack.coffig.spi.ConfigurationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class LegacyConfigurationProvider implements ConfigurationProvider {
    private static final String SEEDSTACK_PREFIX = "org.seedstack.";
    private static final String SEED_PREFIX = SEEDSTACK_PREFIX + "seed.";
    private final Configuration configuration;
    private final List<String> prefixesToStrip = new ArrayList<>();
    private final Map<String, String> conversions = new HashMap<>();

    LegacyConfigurationProvider(Configuration configuration) {
        this.configuration = configuration;
        prefixesToStrip.add(SEED_PREFIX);
        prefixesToStrip.add(SEEDSTACK_PREFIX);
        conversions.put("org.seedstack.jpa.units", "org.seedstack.jpa.unit");
    }

    @Override
    public MapNode provide() {
        MutableMapNode tree = new MutableMapNode();
        configuration.getKeys().forEachRemaining(key -> tree.set(stripPrefixes(key), getValue(configuration, key)));
        postProcess(configuration, tree);
        return tree;
    }

    private void postProcess(Configuration configuration, MutableMapNode tree) {
        configuration.getKeys().forEachRemaining(key -> {
            if (isCandidateForConversion(key)) {
                convert(tree, stripPrefixes(key), stripPrefixes(getConversionPrefix(key)));
            }
        });
    }

    private boolean isCandidateForConversion(String key) {
        return conversions.containsKey(key);
    }

    private String getConversionPrefix(String key) {
        return conversions.get(key);
    }

    private String stripPrefixes(String key) {
        for (String prefix : prefixesToStrip) {
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
            }
        }
        return key;
    }

    private ValueNode getValue(Configuration configuration, String key) {
        return new ValueNode(Arrays.stream(configuration.getStringArray(key)).collect(Collectors.joining(",")));
    }

    private void convert(MutableTreeNode treeNode, String listKey, String itemKey) {
        TreeNode itemNode = treeNode.get(itemKey);
        treeNode.remove(listKey);
        treeNode.remove(itemKey);
        treeNode.set(listKey, itemNode);
    }
}

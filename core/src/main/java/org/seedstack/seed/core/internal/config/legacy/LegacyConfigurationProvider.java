package org.seedstack.seed.core.internal.config.legacy;

import org.apache.commons.configuration.Configuration;
import org.seedstack.coffig.data.ArrayNode;
import org.seedstack.coffig.data.MapNode;
import org.seedstack.coffig.data.TreeNode;
import org.seedstack.coffig.data.ValueNode;
import org.seedstack.coffig.data.mutable.MutableArrayNode;
import org.seedstack.coffig.data.mutable.MutableMapNode;
import org.seedstack.coffig.spi.ConfigurationProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LegacyConfigurationProvider implements ConfigurationProvider {
    public static final String SEEDSTACK_PREFIX = "org.seedstack.";
    public static final String SEED_PREFIX = SEEDSTACK_PREFIX + "seed.";
    private final Configuration configuration;

    public LegacyConfigurationProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public MapNode provide() {
        MutableMapNode tree = new MutableMapNode();
        configuration.getKeys().forEachRemaining(key -> processKey(configuration, key, tree));
        return tree;
    }

    private void processKey(Configuration configuration, String key, MutableMapNode tree) {
        TreeNode treeNode;
        if (isCandidateForConversion(key)) {
            treeNode = convert(configuration, key, getConversionPrefix(key));
        } else {
            treeNode = getValue(configuration, key);
        }
        tree.set(stripPrefixes(key, SEED_PREFIX, SEEDSTACK_PREFIX), treeNode);
    }

    private boolean isCandidateForConversion(String key) {
        return key.equals("org.seedstack.jpa.units");
    }

    private String getConversionPrefix(String key) {
        return "org.seedstack.jpa.unit";
    }

    private String stripPrefixes(String key, String... prefixes) {
        for (String prefix : prefixes) {
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
            }
        }
        return key;
    }

    private ValueNode getValue(Configuration configuration, String key) {
        return new ValueNode(Arrays.stream(configuration.getStringArray(key)).collect(Collectors.joining(",")));
    }

    private ArrayNode convert(Configuration configuration, String keyList, String keyPrefix) {
        MutableArrayNode arrayNode = new MutableArrayNode();
        for (String name : configuration.getStringArray(keyList)) {
            MutableMapNode itemNode = new MutableMapNode();
            String itemKey = String.format("%s.%s", keyPrefix, name);
            configuration.getKeys(itemKey).forEachRemaining(key -> itemNode.set(key.substring((itemKey + ".").length()), getValue(configuration, key)));
            arrayNode.add(itemNode);
        }

        return arrayNode;
    }
}

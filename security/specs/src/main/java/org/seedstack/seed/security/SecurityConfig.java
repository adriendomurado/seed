/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security;

import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Config("security")
public class SecurityConfig {
    private SessionsConfig sessions = new SessionsConfig();
    private List<RealmConfig> realms = new ArrayList<>();
    private Map<String, UserConfig> users = new HashMap<>();
    private Map<String, Set<String>> roles = new HashMap<>();
    private Map<String, Set<String>> permissions = new HashMap<>();

    public SessionsConfig session() {
        return sessions;
    }

    public List<RealmConfig> getRealms() {
        return Collections.unmodifiableList(realms);
    }

    public SecurityConfig addRealm(RealmConfig realmConfig) {
        realms.add(realmConfig);
        return this;
    }

    public Map<String, UserConfig> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    public SecurityConfig addUser(String name, UserConfig userConfig) {
        users.put(name, userConfig);
        return this;
    }

    public Map<String, Set<String>> getRoles() {
        return Collections.unmodifiableMap(roles);
    }

    public SecurityConfig addRole(String name, Set<String> sourceRoles) {
        roles.put(name, sourceRoles);
        return this;
    }

    public Map<String, Set<String>> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    public SecurityConfig addRolePermissions(String role, Set<String> permissions) {
        this.permissions.put(role, permissions);
        return this;
    }

    public static class RealmConfig {
        @SingleValue
        private String name;
        private String roleMapper;
        private String permissionResolver;

        public String getName() {
            return name;
        }

        public RealmConfig setName(String name) {
            this.name = name;
            return this;
        }

        public String getRoleMapper() {
            return roleMapper;
        }

        public RealmConfig setRoleMapper(String roleMapper) {
            this.roleMapper = roleMapper;
            return this;
        }

        public String getPermissionResolver() {
            return permissionResolver;
        }

        public RealmConfig setPermissionResolver(String permissionResolver) {
            this.permissionResolver = permissionResolver;
            return this;
        }
    }

    public static class UserConfig {
        @SingleValue
        private String password = "";
        private Set<String> roles = new HashSet<>();

        public String getPassword() {
            return password;
        }

        public UserConfig setPassword(String password) {
            this.password = password;
            return this;
        }

        public Set<String> getRoles() {
            return Collections.unmodifiableSet(roles);
        }

        public UserConfig addRole(String role) {
            this.roles.add(role);
            return this;
        }
    }

    @Config("sessions")
    public static class SessionsConfig {
        @SingleValue
        private boolean enabled;
        private long timeout = 1000 * 60 * 15;

        public boolean isEnabled() {
            return enabled;
        }

        public SessionsConfig setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public long getTimeout() {
            return timeout;
        }

        public SessionsConfig setTimeout(long timeout) {
            this.timeout = timeout * 1000;
            return this;
        }
    }
}

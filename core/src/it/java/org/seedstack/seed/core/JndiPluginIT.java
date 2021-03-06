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
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seedstack.seed.FromContext;
import org.seedstack.seed.core.fixtures.Service1;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.NamingException;

public class JndiPluginIT {
    @Rule
    public SeedITRule rule = new SeedITRule(this);
    private Injector injector;

    static class Holder {
        @Inject
        Context defaultCtx;

        @Inject
        @Named("defaultContext")
        Context defaultCtxViaName;

        @Inject
        @Named("test1")
        Context ctx1;

        @Inject
        @Named("test2")
        Context ctx2;

        @Resource(name = "org.seedstack.seed.core.fixtures.Service/Service1")
        Service1 service1Default;

        @Resource(name = "org.seedstack.seed.core.fixtures.Service1")
        @FromContext("test2")
        Service1 service1Named;
    }

    @Before
    public void before() {
        injector = rule.getKernel().objectGraph().as(Injector.class).createChildInjector((Module) binder -> binder.bind(Holder.class));
    }

    @Test
    public void jndi_context_injection_is_working() throws Exception {
        Holder holder = injector.getInstance(Holder.class);
        Assertions.assertThat(holder.defaultCtx).isNotNull();
        Assertions.assertThat(holder.defaultCtxViaName).isNotNull();
        Assertions.assertThat(holder.ctx1).isNotNull();
        Assertions.assertThat(holder.ctx2).isNotNull();
        Assertions.assertThat(holder.defaultCtx).isSameAs(holder.defaultCtxViaName);
    }

    @Test
    public void jndi_context_lookup_is_working() throws NamingException {
        Service1 service1 = (Service1) injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service1");
        Assertions.assertThat(service1).isNotNull();
    }

    @Test
    public void jndi_context_named_lookup_is_working() throws NamingException {
        Service1 service1ByClassAndName = (Service1) injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service/Service1");
        Service1 service1ByClass = (Service1) injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service1");

        Assertions.assertThat(service1ByClassAndName).isNotNull();
        Assertions.assertThat(service1ByClassAndName).isSameAs(service1ByClass);
    }

    @Test
    public void two_identically_configured_jndi_contexts_are_not_the_same_instance() throws NamingException {
        Holder holder = injector.getInstance(Holder.class);
        Assertions.assertThat(holder.ctx1).isNotSameAs(holder.ctx2);
    }

    @Test
    public void explicit_resource_injection_from_default_context_is_working() throws NamingException {
        Holder holder = injector.getInstance(Holder.class);
        Assertions.assertThat(holder.service1Default).isNotNull();
        Assertions.assertThat(holder.service1Named).isEqualTo(injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service1"));
    }

    @Test
    public void explicit_resource_injection_from_named_context_is_working() throws NamingException {
        Holder holder = injector.getInstance(Holder.class);
        Assertions.assertThat(holder.service1Named).isNotNull();
        Assertions.assertThat(holder.service1Named).isEqualTo(injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service1"));
    }

    @Test
    public void implicit_resource_injection_is_working() throws NamingException {
        Holder holder = injector.getInstance(Holder.class);
        Assertions.assertThat(holder.service1Default).isNotNull();
        Assertions.assertThat(holder.service1Named).isEqualTo(injector.getInstance(Holder.class).ctx1.lookup("org.seedstack.seed.core.fixtures.Service1"));
    }
}

/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.rest.internal;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import org.kametic.specifications.Specification;
import org.seedstack.seed.core.SeedRuntime;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.rest.RelRegistry;
import org.seedstack.seed.rest.RestConfig;
import org.seedstack.seed.rest.internal.exceptionmapper.AuthenticationExceptionMapper;
import org.seedstack.seed.rest.internal.exceptionmapper.AuthorizationExceptionMapper;
import org.seedstack.seed.rest.internal.exceptionmapper.InternalErrorExceptionMapper;
import org.seedstack.seed.rest.internal.exceptionmapper.WebApplicationExceptionMapper;
import org.seedstack.seed.rest.internal.hal.RelRegistryImpl;
import org.seedstack.seed.rest.internal.jsonhome.JsonHome;
import org.seedstack.seed.rest.internal.jsonhome.JsonHomeRootResource;
import org.seedstack.seed.rest.internal.jsonhome.Resource;
import org.seedstack.seed.rest.spi.RestProvider;
import org.seedstack.seed.rest.spi.RootResource;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class RestPlugin extends AbstractSeedPlugin implements RestProvider {
    static final Specification<Class<?>> resourcesSpecification = new JaxRsResourceSpecification();
    static final Specification<Class<?>> providersSpecification = new JaxRsProviderSpecification();

    private final Map<Variant, Class<? extends RootResource>> rootResourcesByVariant = new HashMap<>();
    private RestConfig restConfig;
    private boolean enabled;
    private ServletContext servletContext;
    private RelRegistry relRegistry;
    private JsonHome jsonHome;
    private Collection<Class<?>> resources;
    private Collection<Class<?>> providers;

    @Override
    public String name() {
        return "rest";
    }

    @Override
    protected void setup(SeedRuntime seedRuntime) {
        servletContext = seedRuntime.contextAs(ServletContext.class);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .specification(providersSpecification)
                .specification(resourcesSpecification)
                .build();
    }

    @Override
    public InitState initialize(InitContext initContext) {
        Map<Specification, Collection<Class<?>>> scannedClasses = initContext.scannedTypesBySpecification();

        restConfig = getConfiguration(RestConfig.class);
        resources = scannedClasses.get(RestPlugin.resourcesSpecification);
        providers = scannedClasses.get(RestPlugin.providersSpecification);

        initializeHypermedia();

        if (servletContext != null) {
            addJacksonProviders(providers);

            configureExceptionMappers();

            if (restConfig.isJsonHome()) {
                // The typed locale parameter resolves constructor ambiguity when the JAX-RS 2.0 spec is in the classpath
                addRootResourceVariant(new Variant(new MediaType("application", "json"), (Locale) null, null), JsonHomeRootResource.class);
            }

            enabled = true;
        }

        return InitState.INITIALIZED;
    }

    private void configureExceptionMappers() {
        if (!restConfig.exceptionMapping().isSecurity()) {
            providers.remove(AuthenticationExceptionMapper.class);
            providers.remove(AuthorizationExceptionMapper.class);
        }

        if (!restConfig.exceptionMapping().isAll()) {
            providers.remove(WebApplicationExceptionMapper.class);
            providers.remove(InternalErrorExceptionMapper.class);
        }
    }

    private void addJacksonProviders(Collection<Class<?>> providers) {
        providers.add(JsonMappingExceptionMapper.class);
        providers.add(JsonParseExceptionMapper.class);
        providers.add(JacksonJsonProvider.class);
        providers.add(JacksonJaxbJsonProvider.class);
    }

    private void initializeHypermedia() {
        ResourceScanner resourceScanner = new ResourceScanner(restConfig, servletContext).scan(resources);
        Map<String, Resource> resourceMap = resourceScanner.jsonHomeResources();

        relRegistry = new RelRegistryImpl(resourceScanner.halLinks());
        jsonHome = new JsonHome(resourceMap);
    }

    @Override
    public Object nativeUnitModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                install(new HypermediaModule(jsonHome, relRegistry));
                if (enabled) {
                    install(new RestModule(restConfig, filterResourceClasses(resources), providers, rootResourcesByVariant));
                }
            }
        };
    }

    private Collection<Class<?>> filterResourceClasses(Collection<Class<?>> resourceClasses) {
        if (!rootResourcesByVariant.isEmpty()) {
            return resourceClasses;
        } else {
            HashSet<Class<?>> filteredResourceClasses = new HashSet<>(resourceClasses);
            filteredResourceClasses.remove(RootResourceDispatcher.class);
            return filteredResourceClasses;
        }
    }

    public void addRootResourceVariant(Variant variant, Class<? extends RootResource> rootResource) {
        rootResourcesByVariant.put(variant, rootResource);
    }

    public RestConfig getRestConfig() {
        return restConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Set<Class<?>> resources() {
        return resources != null ? new HashSet<>(filterResourceClasses(resources)) : new HashSet<>();
    }

    @Override
    public Set<Class<?>> providers() {
        return providers != null ? new HashSet<>(providers) : new HashSet<>();
    }
}

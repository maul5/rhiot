/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.kura.router;

import org.apache.camel.component.kura.KuraRouter;
import org.apache.camel.model.RoutesDefinition;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * Base class for Camel routers deployable into Eclipse Kura. All RhiotKuraRouters are configurable components managable
 * from the Everyware cloud.
 */
public abstract class RhiotKuraRouter extends KuraRouter implements ConfigurableComponent {

    // Members

    /**
     * Camel route XML, usually configured using SCR property.
     */
    private String camelRouteXml;

    // Getters & setters

    public String getCamelRouteXml() {
        return camelRouteXml;
    }

    public void setCamelRouteXml(String camelRouteXml) {
        this.camelRouteXml = camelRouteXml;
        if(camelRouteXml != null && !camelRouteXml.isEmpty()) {
            try {
                RoutesDefinition routesDefinition = camelContext.loadRoutesDefinition(new ByteArrayInputStream(camelRouteXml.getBytes()));
                camelContext.addRouteDefinitions(routesDefinition.getRoutes());
            } catch (Exception e) {
                log.warn("Cannot load routes definitions: {}", camelRouteXml);
            }
        }
    }

    // ASF Camel workarounds

    // TODO: Remove this overridden method as soon as Camel 2.17 is out (see CAMEL-9357)
    @Override
    public void configure() throws Exception {
        log.debug("No programmatic routes configuration found.");
    }

    // TODO: Remove this overridden method as soon as Camel 2.17 is out (see CAMEL-9314)
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            super.start(bundleContext);
        } catch (Throwable e) {
            String errorMessage = "Problem when starting Kura module " + getClass().getName() + ":";
            log.warn(errorMessage, e);

            // Print error to the Kura console.
            System.err.println(errorMessage);
            e.printStackTrace();

            throw e;
        }
    }

    // TODO: Remove these overridden methods as soon as Camel 2.17 is out (see CAMEL-9351)

    protected void activate(ComponentContext componentContext, Map<String,Object> properties) throws Exception {
        start(componentContext.getBundleContext());
    }

    protected void deactivate(ComponentContext componentContext) throws Exception{
        stop(componentContext.getBundleContext());
    }

}
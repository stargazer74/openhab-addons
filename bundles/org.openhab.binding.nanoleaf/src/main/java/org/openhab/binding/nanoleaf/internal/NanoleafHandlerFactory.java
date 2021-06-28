/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.nanoleaf.internal;

import static org.openhab.binding.nanoleaf.internal.NanoleafBindingConstants.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.nanoleaf.internal.handler.NanoleafControllerHandler;
import org.openhab.binding.nanoleaf.internal.handler.NanoleafPanelHandler;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NanoleafHandlerFactory} is responsible for creating the controller (bridge)
 * and panel (thing) handlers.
 *
 * @author Martin Raepple - Initial contribution
 * @author Kai Kreuzer - made discovery a handler service
 */
@NonNullByDefault
@Component(configurationPid = "binding.nanoleaf", service = ThingHandlerFactory.class)
public class NanoleafHandlerFactory extends BaseThingHandlerFactory {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_LIGHT_PANEL, THING_TYPE_CONTROLLER).collect(Collectors.toSet()));

    private final Logger logger = LoggerFactory.getLogger(NanoleafHandlerFactory.class);
    private final HttpClient httpClient;

    @Activate
    public NanoleafHandlerFactory(@Reference final HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_CONTROLLER.equals(thingTypeUID)) {
            NanoleafControllerHandler handler = new NanoleafControllerHandler((Bridge) thing, httpClient);
            logger.debug("Nanoleaf controller handler created.");
            return handler;
        } else if (THING_TYPE_LIGHT_PANEL.equals(thingTypeUID)) {
            NanoleafPanelHandler handler = new NanoleafPanelHandler(thing, httpClient);
            logger.debug("Nanoleaf panel handler created.");
            return handler;
        }
        return null;
    }
}

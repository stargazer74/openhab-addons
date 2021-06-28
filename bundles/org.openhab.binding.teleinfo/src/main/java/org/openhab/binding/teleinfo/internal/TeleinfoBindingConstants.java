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
package org.openhab.binding.teleinfo.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link TeleinfoBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nicolas SIBERIL - Initial contribution
 */
@NonNullByDefault
public class TeleinfoBindingConstants {

    private TeleinfoBindingConstants() {
    }

    private static final String BINDING_ID = "teleinfo";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SERIAL_CONTROLLER = new ThingTypeUID(BINDING_ID, "serialcontroller");
    public static final String THING_SERIAL_CONTROLLER_CHANNEL_INVALID_FRAME_COUNTER = "invalidFrameCounter";

    // List of commons channel ids
    public static final String CHANNEL_LAST_UPDATE = "lastUpdate";
    // List of HC frames channel ids
    public static final String CHANNEL_HC_FRAME_HCHC = "hchc";
    public static final String CHANNEL_HC_FRAME_HCHP = "hchp";
    public static final String CHANNEL_HHPHC = "hhphc";
    // List of BASE frames channel ids
    public static final String CHANNEL_BASE_FRAME_BASE = "base";
    // List of TEMPO frames channel ids
    public static final String CHANNEL_TEMPO_FRAME_BBRHPJR = "bbrhpjr";
    public static final String CHANNEL_TEMPO_FRAME_BBRHCJR = "bbrhcjr";
    public static final String CHANNEL_TEMPO_FRAME_BBRHPJW = "bbrhpjw";
    public static final String CHANNEL_TEMPO_FRAME_BBRHCJW = "bbrhcjw";
    public static final String CHANNEL_TEMPO_FRAME_BBRHPJB = "bbrhpjb";
    public static final String CHANNEL_TEMPO_FRAME_BBRHCJB = "bbrhcjb";
    public static final String CHANNEL_TEMPO_FRAME_DEMAIN = "demain";
    public static final String CHANNEL_TEMPO_FRAME_PROGRAMME_CIRCUIT_1 = "programmeCircuit1";
    public static final String CHANNEL_TEMPO_FRAME_PROGRAMME_CIRCUIT_2 = "programmeCircuit2";
    // List of EJP frames channel ids
    public static final String CHANNEL_EJP_FRAME_PEJP = "pejp";
    public static final String CHANNEL_EJP_FRAME_EJPHPM = "ejphpm";
    public static final String CHANNEL_EJP_FRAME_EJPHN = "ejphn";
    // List of CBEMM Channel ids
    public static final String CHANNEL_ISOUSC = "isousc";
    public static final String CHANNEL_PTEC = "ptec";
    public static final String CHANNEL_CBEMM_IMAX = "imax";
    public static final String CHANNEL_CBEMM_ADPS = "adps";
    public static final String CHANNEL_CBEMM_IINST = "iinst";
    public static final String CHANNEL_MOTDETAT = "motdetat";
    // List of CBEMM EVOLUTION ICC Channel ids
    public static final String CHANNEL_PAPP = "papp";
    // List of CBETM Channel ids
    public static final String CHANNEL_CBETM_IINST1 = "iinst1";
    public static final String CHANNEL_CBETM_IINST2 = "iinst2";
    public static final String CHANNEL_CBETM_IINST3 = "iinst3";
    public static final String CHANNEL_CBETM_FRAME_TYPE = "frameType";
    public static final String CHANNEL_CBETM_LONG_IMAX1 = "imax1";
    public static final String CHANNEL_CBETM_LONG_IMAX2 = "imax2";
    public static final String CHANNEL_CBETM_LONG_IMAX3 = "imax3";
    public static final String CHANNEL_CBETM_LONG_PMAX = "pmax";
    public static final String CHANNEL_CBETM_LONG_PPOT = "ppot";
    public static final String CHANNEL_CBETM_SHORT_ADIR1 = "adir1";
    public static final String CHANNEL_CBETM_SHORT_ADIR2 = "adir2";
    public static final String CHANNEL_CBETM_SHORT_ADIR3 = "adir3";

    public static final String NOT_A_CHANNEL = "";

    public static final String THING_ELECTRICITY_METER_PROPERTY_ADCO = "adco";

    public static final ThingTypeUID THING_HC_CBEMM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_hc_electricitymeter");

    public static final ThingTypeUID THING_BASE_CBEMM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_base_electricitymeter");

    public static final ThingTypeUID THING_EJP_CBEMM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_ejp_electricitymeter");

    public static final ThingTypeUID THING_TEMPO_CBEMM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_tempo_electricitymeter");

    public static final ThingTypeUID THING_HC_CBEMM_EVO_ICC_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_evolution_icc_hc_electricitymeter");

    public static final ThingTypeUID THING_BASE_CBEMM_EVO_ICC_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_evolution_icc_base_electricitymeter");

    public static final ThingTypeUID THING_EJP_CBEMM_EVO_ICC_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_evolution_icc_ejp_electricitymeter");

    public static final ThingTypeUID THING_TEMPO_CBEMM_EVO_ICC_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbemm_evolution_icc_tempo_electricitymeter");

    public static final ThingTypeUID THING_HC_CBETM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbetm_hc_electricitymeter");

    public static final ThingTypeUID THING_BASE_CBETM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbetm_base_electricitymeter");

    public static final ThingTypeUID THING_EJP_CBETM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbetm_ejp_electricitymeter");

    public static final ThingTypeUID THING_TEMPO_CBETM_ELECTRICITY_METER_TYPE_UID = new ThingTypeUID(BINDING_ID,
            "cbetm_tempo_electricitymeter");

    public static final String ERROR_OFFLINE_SERIAL_NOT_FOUND = "@text/teleinfo.thingstate.serial_notfound";
    public static final String ERROR_OFFLINE_SERIAL_INUSE = "@text/teleinfo.thingstate.serial_inuse";
    public static final String ERROR_OFFLINE_SERIAL_UNSUPPORTED = "@text/teleinfo.thingstate.serial_unsupported";
    public static final String ERROR_OFFLINE_SERIAL_LISTENERS = "@text/teleinfo.thingstate.serial_listeners";
    public static final String ERROR_OFFLINE_CONTROLLER_OFFLINE = "@text/teleinfo.thingstate.controller_offline";
    public static final String ERROR_UNKNOWN_RETRY_IN_PROGRESS = "@text/teleinfo.thingstate.controller_unknown_retry_inprogress";
}

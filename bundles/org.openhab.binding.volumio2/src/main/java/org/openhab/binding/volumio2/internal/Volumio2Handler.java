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
package org.openhab.binding.volumio2.internal;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.openhab.binding.volumio2.internal.mapping.Volumio2Data;
import org.openhab.binding.volumio2.internal.mapping.Volumio2Events;
import org.openhab.binding.volumio2.internal.mapping.Volumio2ServiceTypes;
import org.openhab.core.library.types.NextPreviousType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PlayPauseType;
import org.openhab.core.library.types.RewindFastforwardType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * The {@link Volumio2Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Patrick Sernetz - Initial Contribution
 * @author Chris Wohlbrecht - Adaption for openHAB 3
 */
public class Volumio2Handler extends BaseThingHandler {

    private static final Logger logger = LoggerFactory.getLogger(Volumio2Handler.class);

    private Volumio2Service volumio;

    private final Volumio2Data state = new Volumio2Data();

    public Volumio2Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        logger.debug("channelUID: {}", channelUID);

        if (volumio == null) {
            logger.debug("Ignoring command {} = {} because device is offline.", channelUID.getId(), command);
            if (ThingStatus.ONLINE.equals(getThing().getStatus())) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "device is offline");
            }
            return;
        }

        try {
            switch (channelUID.getId()) {
                case Volumio2BindingConstants.CHANNEL_PLAYER:
                    handlePlaybackCommands(command);
                    break;
                case Volumio2BindingConstants.CHANNEL_VOLUME:
                    handleVolumeCommand(command);
                    break;

                case Volumio2BindingConstants.CHANNEL_ARTIST:
                    break;
                case Volumio2BindingConstants.CHANNEL_ALBUM:
                    break;
                case Volumio2BindingConstants.CHANNEL_TRACK_TYPE:
                    break;
                /**
                 * case CHANNEL_COVER_ART:
                 * if (command instanceof RefreshType) {
                 * volumio.getState();
                 * }
                 * break;
                 **/
                case Volumio2BindingConstants.CHANNEL_TITLE:
                    break;

                case Volumio2BindingConstants.CHANNEL_PLAY_RADIO_STREAM:
                    if (command instanceof StringType) {
                        final String uri = command.toFullString();
                        volumio.replacePlay(uri, "Radio", Volumio2ServiceTypes.WEBRADIO);
                    }
                    break;

                case Volumio2BindingConstants.CHANNEL_PLAY_URI:
                    if (command instanceof StringType) {
                        final String uri = command.toFullString();
                        volumio.replacePlay(uri, "URI", Volumio2ServiceTypes.WEBRADIO);
                    }
                    break;

                case Volumio2BindingConstants.CHANNEL_PLAY_FILE:
                    if (command instanceof StringType) {
                        final String uri = command.toFullString();
                        volumio.replacePlay(uri, "", Volumio2ServiceTypes.MPD);
                    }
                    break;

                case Volumio2BindingConstants.CHANNEL_PLAY_PLAYLIST:
                    if (command instanceof StringType) {
                        final String playlistName = command.toFullString();
                        volumio.playPlaylist(playlistName);
                    }
                    break;
                case Volumio2BindingConstants.CHANNEL_CLEAR_QUEUE:
                    if (command instanceof OnOffType) {
                        if (command == OnOffType.ON) {
                            volumio.clearQueue();
                            // Make it feel like a toggle button ...
                            updateState(channelUID, OnOffType.OFF);
                        }
                    }
                    break;
                case Volumio2BindingConstants.CHANNEL_PLAY_RANDOM:
                    if (command instanceof OnOffType) {
                        boolean enableRandom = command == OnOffType.ON;
                        volumio.setRandom(enableRandom);
                    }
                    break;
                case Volumio2BindingConstants.CHANNEL_PLAY_REPEAT:
                    if (command instanceof OnOffType) {
                        boolean enableRepeat = command == OnOffType.ON;
                        volumio.setRepeat(enableRepeat);
                    }
                    break;
                case "REFRESH":
                    logger.debug("Called Refresh");
                    volumio.getState();
                    break;
                case Volumio2BindingConstants.CHANNEL_SYSTEM_COMMAND:
                    if (command instanceof StringType) {
                        sendSystemCommand(command);
                        updateState(Volumio2BindingConstants.CHANNEL_SYSTEM_COMMAND, UnDefType.UNDEF);
                    } else if (RefreshType.REFRESH == command) {
                        updateState(Volumio2BindingConstants.CHANNEL_SYSTEM_COMMAND, UnDefType.UNDEF);
                    }
                    break;
                case Volumio2BindingConstants.CHANNEL_STOP:
                    if (command instanceof StringType) {
                        handleStopCommand(command);
                        updateState(Volumio2BindingConstants.CHANNEL_STOP, UnDefType.UNDEF);
                    } else if (RefreshType.REFRESH == command) {
                        updateState(Volumio2BindingConstants.CHANNEL_STOP, UnDefType.UNDEF);
                    }
                    break;
                default:
                    logger.error("Unknown channel: {}", channelUID.getId());
            }

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    private void sendSystemCommand(Command command) {
        if (command instanceof StringType) {
            volumio.sendSystemCommand(command.toString());
            updateState(Volumio2BindingConstants.CHANNEL_SYSTEM_COMMAND, UnDefType.UNDEF);
        } else if (command.equals(RefreshType.REFRESH)) {
            updateState(Volumio2BindingConstants.CHANNEL_SYSTEM_COMMAND, UnDefType.UNDEF);
        }
    }

    /**
     * Set all channel of thing to UNDEF during connection.
     */
    private void clearChannels() {
        for (Channel channel : getThing().getChannels()) {
            updateState(channel.getUID(), UnDefType.UNDEF);
        }
    }

    private void handleVolumeCommand(Command command) {

        if (command instanceof PercentType) {
            volumio.setVolume((PercentType) command);
        } else if (command instanceof RefreshType) {
            volumio.getState();
        } else {
            logger.error("Command is not handled");
        }
    }

    private void handleStopCommand(Command command) {
        if (command instanceof StringType) {
            volumio.stop();
            updateState(Volumio2BindingConstants.CHANNEL_STOP, UnDefType.UNDEF);
        } else if (command.equals(RefreshType.REFRESH)) {
            updateState(Volumio2BindingConstants.CHANNEL_STOP, UnDefType.UNDEF);
        }
    }

    private void handlePlaybackCommands(Command command) {
        if (command instanceof PlayPauseType) {

            PlayPauseType playPauseCmd = (PlayPauseType) command;

            switch (playPauseCmd) {
                case PLAY:
                    volumio.play();
                    break;
                case PAUSE:
                    volumio.pause();
                    break;
            }
        } else if (command instanceof NextPreviousType) {

            NextPreviousType nextPreviousType = (NextPreviousType) command;

            switch (nextPreviousType) {
                case PREVIOUS:
                    volumio.previous();
                    break;
                case NEXT:
                    volumio.next();
                    break;
            }

        } else if (command instanceof RewindFastforwardType) {

            RewindFastforwardType fastForwardType = (RewindFastforwardType) command;

            switch (fastForwardType) {
                case FASTFORWARD:
                case REWIND:
                    logger.warn("Not implemented yet");
                    break;
            }

        } else if (command instanceof RefreshType) {
            volumio.getState();
        } else {
            logger.error("Command is not handled: {}", command);
        }
    }

    /**
     * Bind default listeners to volumio session.
     * - EVENT_CONNECT - Connection to volumio was established
     * - EVENT_DISCONNECT - Connection was disconnected
     * - PUSH.STATE -
     */
    private void bindDefaultListener() {

        volumio.on(Socket.EVENT_CONNECT, connectListener());
        volumio.on(Socket.EVENT_DISCONNECT, disconnectListener());
        volumio.on(Volumio2Events.PUSH_STATE, pushStateListener());
    }

    /**
     * Read the configuration and connect to volumio device. The Volumio impl. is
     * async so it should not block the process in any way.
     */
    @Override
    public void initialize() {

        String hostname = (String) getThing().getConfiguration().get(Volumio2BindingConstants.CONFIG_PROPERTY_HOSTNAME);
        int port = ((BigDecimal) getThing().getConfiguration().get(Volumio2BindingConstants.CONFIG_PROPERTY_PORT))
                .intValueExact();
        String protocol = (String) getThing().getConfiguration().get(Volumio2BindingConstants.CONFIG_PROPERTY_PROTOCOL);
        int timeout = ((BigDecimal) getThing().getConfiguration().get(Volumio2BindingConstants.CONFIG_PROPERTY_TIMEOUT))
                .intValueExact();

        if (hostname == null) {

            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration incomplete, missing hostname");

        } else if (protocol == null) {

            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration incomplete, missing protocol");

        } else {

            logger.debug("Trying to connect to Volumio2 on {}://{}:{}", protocol, hostname, port);
            try {
                volumio = new Volumio2Service(protocol, hostname, port, timeout);

                clearChannels();
                bindDefaultListener();
                updateStatus(ThingStatus.OFFLINE);
                volumio.connect();

            } catch (Exception e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }

        }
    }

    @Override
    public void dispose() {
        if (volumio != null) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (volumio.isConnected()) {
                        logger.warn("Timeout during disconnect event");
                    } else {
                        volumio.close();
                    }
                    clearChannels();
                }
            }, 30, TimeUnit.SECONDS);

            volumio.disconnect();

        }
    }

    public void playURI(StringType url) {
        logger.debug("Play uri sound: {}", url.toFullString());
        this.volumio.playURI(url.toFullString());
    }

    public void playNotificationSoundURI(StringType url) {
        logger.debug("Play notification sound: {}", url.toFullString());
    }

    /** Listener **/

    /**
     * As soon as the Connect Listener is executed
     * the ThingStatus is set to ONLINE.
     *
     * @return
     */
    private Emitter.Listener connectListener() {
        return arg -> updateStatus(ThingStatus.ONLINE);
    }

    /**
     * As soon as the Disconnect Listener is executed
     * the ThingStatus is set to OFFLINE.
     *
     * @return
     */
    private Emitter.Listener disconnectListener() {
        return arg0 -> updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
    }

    /**
     * On received a pushState Event, the ThingChannels are
     * updated if there is a change and they are linked.
     *
     * @return
     */
    private Emitter.Listener pushStateListener() {
        return data -> {

            try {

                JSONObject jsonObject = (JSONObject) data[0];
                logger.debug("{}", jsonObject.toString());
                state.update(jsonObject);

                if (isLinked(Volumio2BindingConstants.CHANNEL_TITLE) && state.isTitleDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_TITLE, state.getTitle());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_ARTIST) && state.isArtistDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_ARTIST, state.getArtist());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_ALBUM) && state.isAlbumDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_ALBUM, state.getAlbum());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_VOLUME) && state.isVolumeDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_VOLUME, state.getVolume());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_PLAYER) && state.isStateDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_PLAYER, state.getState());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_TRACK_TYPE) && state.isTrackTypeDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_TRACK_TYPE, state.getTrackType());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_PLAY_RANDOM) && state.isRandomDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_PLAY_RANDOM, state.getRandom());
                }

                if (isLinked(Volumio2BindingConstants.CHANNEL_PLAY_REPEAT) && state.isRepeatDirty()) {
                    updateState(Volumio2BindingConstants.CHANNEL_PLAY_REPEAT, state.getRepeat());
                }

                /**
                 * if (isLinked(CHANNEL_COVER_ART) && state.isCoverArtDirty()) {
                 * updateState(CHANNEL_COVER_ART, state.getCoverArt());
                 * }
                 */

            } catch (JSONException e) {
                logger.error("Could not refresh channel: {}", e.getMessage());

            }
        };
    }
}

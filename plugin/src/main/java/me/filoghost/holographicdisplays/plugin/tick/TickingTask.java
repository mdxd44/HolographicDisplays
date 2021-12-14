/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.tick;

import me.filoghost.fcommons.logging.Log;
import me.filoghost.holographicdisplays.plugin.hologram.tracking.LineTrackerManager;
import me.filoghost.holographicdisplays.plugin.listener.LineClickListener;
import me.filoghost.holographicdisplays.plugin.placeholder.tracking.ActivePlaceholderTracker;

public class TickingTask implements Runnable {

    private final TickClock tickClock;
    private final ActivePlaceholderTracker placeholderTracker;
    private final LineTrackerManager lineTrackerManager;
    private final LineClickListener lineClickListener;

    private long lastErrorLogTick;

    public TickingTask(
            TickClock tickClock,
            ActivePlaceholderTracker placeholderTracker,
            LineTrackerManager lineTrackerManager,
            LineClickListener lineClickListener) {
        this.tickClock = tickClock;
        this.placeholderTracker = placeholderTracker;
        this.lineTrackerManager = lineTrackerManager;
        this.lineClickListener = lineClickListener;
    }

    @Override
    public void run() {
        tickClock.incrementTick();

        // Remove outdated entries before using them from line trackers
        placeholderTracker.clearOutdatedEntries();

        try {
            lineTrackerManager.update();
        } catch (Throwable t) {
            // Catch all types of Throwable because we're using NMS code
            if (tickClock.getCurrentTick() - lastErrorLogTick >= 20) {
                // Avoid spamming the console, log the error at most once every 20 ticks
                lastErrorLogTick = tickClock.getCurrentTick();
                Log.severe("Error while ticking holograms", t);
            }
        }

        // Remove placeholders which were not used by line trackers
        placeholderTracker.clearInactivePlaceholders();

        lineClickListener.processQueuedClickEvents();
    }

}

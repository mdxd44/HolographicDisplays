/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.hologram.api;

import me.filoghost.holographicdisplays.api.hologram.line.CollectableHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.PickupListener;
import me.filoghost.holographicdisplays.plugin.hologram.base.Collectable;
import org.bukkit.entity.Player;

public interface APICollectableHologramLine extends CollectableHologramLine, APIHologramLine, Collectable {

    @Override
    default boolean hasPickupCallback() {
        return getPickupListener() != null;
    }

    @Override
    default void invokePickupCallback(Player player) {
        try {
            PickupListener pickupListener = getPickupListener();
            if (pickupListener != null) {
                pickupListener.onPickup(player);
            }
        } catch (Throwable t) {
            logPickupCallbackException(getCreatorPlugin(), player, t);
        }
    }

}

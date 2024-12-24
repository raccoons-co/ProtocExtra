/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.common.eventbus;
/**
 *
 * Adds functionality of registering and unregistering subscriber methods.
 */
public interface Subscribable extends Broadcastable {

    /**
     * Registers all subscriber methods on object to receive events.
     */
    default void register() {
        Bus.instance().register(this);
    }

    /**
     * Unregisters all subscriber methods on a registered object.
     */
    default void unregister() {
        Bus.instance().unregister(this);
    }
}

/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.common.eventbus;

/**
 * Adds functionality of posting event to all registered subscribers.
 */
public interface Observable extends Broadcastable {

    /**
     * Posts an event to all registered subscribers.
     */
    default void post() {
        Bus.instance().post(this);
    }
}

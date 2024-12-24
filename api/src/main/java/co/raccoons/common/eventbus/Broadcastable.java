/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.common.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * Abstract interface introduced for the inheritance that adds functionality of
 * operating with event bus.
 */
interface Broadcastable {

    /**
     * The event bus.
     */
    final class Bus {

        private static final EventBus INSTANCE = new EventBus();

        private Bus() {
        }

        /**
         * Returns a singleton instance.
         */
        public static EventBus instance() {
            return INSTANCE;
        }
    }
}

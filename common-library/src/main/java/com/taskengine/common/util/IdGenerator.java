package com.taskengine.common.util;

import java.util.UUID;

/**
 * Centralized ID generation utility.
 *
 * <p>Uses UUID v4 for globally unique, collision-resistant identifiers
 * across distributed nodes without coordination.
 */
public final class IdGenerator {

    private IdGenerator() {
        // Utility class — prevent instantiation
    }

    /**
     * Generates a new UUID string for use as entity identifiers.
     *
     * @return a new UUID string
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a new UUID object.
     *
     * @return a new UUID
     */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
}

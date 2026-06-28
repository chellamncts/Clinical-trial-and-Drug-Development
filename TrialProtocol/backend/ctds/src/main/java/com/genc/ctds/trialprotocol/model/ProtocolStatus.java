package com.genc.ctds.trialprotocol.model;

public enum ProtocolStatus {
    DRAFT,
    APPROVED,
    ACTIVE,
    CLOSED;

    /**
     * Defines the legal protocol lifecycle transitions.
     * DRAFT -> APPROVED -> ACTIVE -> CLOSED (with APPROVED/DRAFT able to close early).
     * ACTIVE is reached automatically when a site is activated.
     */
    public boolean canTransitionTo(ProtocolStatus target) {
        return switch (this) {
            case DRAFT -> target == APPROVED || target == CLOSED;
            case APPROVED -> target == ACTIVE || target == CLOSED;
            case ACTIVE -> target == CLOSED;
            case CLOSED -> false;
        };
    }

    /** Sites may only be registered/activated when the protocol is in one of these states. */
    public boolean allowsSiteWork() {
        return this == APPROVED || this == ACTIVE;
    }
}

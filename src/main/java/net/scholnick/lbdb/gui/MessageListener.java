package net.scholnick.lbdb.gui;

/**
 * MessageListener interface for sending messages.
 */
@FunctionalInterface
public interface MessageListener {
    /** Sends a message. */
    void send(String message);

    /** Clears the message. */
    default void clear() {
        send(" ");
    }
}

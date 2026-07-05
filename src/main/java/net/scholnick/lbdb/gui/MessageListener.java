package net.scholnick.lbdb.gui;

/**
 * MessageListener interface for sending messages.
 */
@FunctionalInterface
public interface MessageListener {
    void send(String message);

    default void clear() {
        send(" ");
    }
}

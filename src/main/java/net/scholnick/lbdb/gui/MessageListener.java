package net.scholnick.lbdb.gui;

/**
 * MessageListener interface for sending messages.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@FunctionalInterface
public interface MessageListener {
    void send(String message);
}

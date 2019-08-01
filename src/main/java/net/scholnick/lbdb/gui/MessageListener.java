package net.scholnick.lbdb.gui;

@FunctionalInterface
public interface MessageListener {
    void send(String message);
}

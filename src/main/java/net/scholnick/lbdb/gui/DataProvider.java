package net.scholnick.lbdb.gui;

import java.util.List;

@FunctionalInterface
public interface DataProvider {
    List<String> search(String text);
}

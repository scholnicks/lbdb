package net.scholnick.lbdb.gui;

import java.util.List;

/**
 * A functional interface for providing data based on a search query.
 */
@FunctionalInterface
public interface DataProvider {
    /**
     * Searches for data based on the provided text.
     *
     * @param text the search query
     * @return a list of strings that match the search query
     */
    List<String> search(String text);
}

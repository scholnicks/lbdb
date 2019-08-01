package net.scholnick.lbdb.gui.autocomplete;

import java.util.List;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface DataSupplier<T> {
    /** Searches for data and returns the results */
    List<T> search(String criteria);
}

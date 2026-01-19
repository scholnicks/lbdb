package net.scholnick.lbdb.gui.title;

import java.util.EventListener;

/**
 * TitleSelectionListener interface for listening to title selection events.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@FunctionalInterface
public interface TitleSelectionListener extends EventListener {
    /** Invoked when a title is selected. */
    void select(TitleSelectionEvent event);
}

package net.scholnick.lbdb.gui.author;

import java.util.EventListener;

/**
 * AuthorSelectionListener is a {@link EventListener} that signals that an author has been selected.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@FunctionalInterface
public interface AuthorSelectionListener extends EventListener {
	/** Invoked when an author has been selected. */
	void select(AuthorSelectionEvent event);
}

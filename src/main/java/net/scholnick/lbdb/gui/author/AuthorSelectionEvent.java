package net.scholnick.lbdb.gui.author;

import java.util.EventObject;

/**
 * AuthorSelectionEvent is a {@link EventObject} that signals that an author has been selected.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class AuthorSelectionEvent extends EventObject {
	/** Constructs a new AuthorSelectionEvent. */
	public AuthorSelectionEvent(Object source) {
		super(source);
	}
}

package net.scholnick.lbdb.gui.author;

import java.util.EventListener;

@FunctionalInterface
public interface AuthorSelectionListener extends EventListener {
	void select(AuthorSelectionEvent event);
}

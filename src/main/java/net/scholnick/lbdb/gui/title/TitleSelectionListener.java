package net.scholnick.lbdb.gui.title;

import java.util.EventListener;

@FunctionalInterface
public interface TitleSelectionListener extends EventListener {
	void select(TitleSelectionEvent event);
}

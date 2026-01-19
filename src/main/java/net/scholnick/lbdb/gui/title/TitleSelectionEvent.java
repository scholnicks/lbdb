package net.scholnick.lbdb.gui.title;

import java.util.EventObject;

/**
 * TitleSelectionEvent is fired when a title is selected in the TitleListPanel.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class TitleSelectionEvent extends EventObject {
    public TitleSelectionEvent(Object source) {
        super(source);
    }
}

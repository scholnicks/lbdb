package net.scholnick.lbdb.util;

import javax.swing.*;
import java.awt.*;

public final class LabelFactory {
	private LabelFactory() {}

	public static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(TEXT_COLOR);
		return label;
	}

	public static JLabel createDataLabel() {
		JLabel label = new JLabel();
		label.setForeground(Color.blue);
		return label;
	}

	/**
	public static JLabel createClickableLabel(String text) {
		JLabel label = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(getForeground());
				g.fillRect(0, getHeight() - 3, getFontMetrics(getFont()).stringWidth(getText()), 1);
			}
			private static final long serialVersionUID = -3003482266928994631L;
		};

		label.setText(text);
		label.setForeground(CLICKABLE_COLOR);
		return label;
	}
   */

	private static final Color TEXT_COLOR = Color.black;
	//private static final Color CLICKABLE_COLOR = new Color(0x00, 0x64, 0x00);
}
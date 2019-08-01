package net.scholnick.lbdb.gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import java.net.URL;

public final class SoundPlayer {
	public static void playAhh() {
		new Thread(() -> {
			playFile(SoundPlayer.class.getClassLoader().getResource("aaaaahhhhh.aif"));
		}).start();
	}

	private static void playFile(URL soundURL) {
		try {
			Line.Info linfo = new Line.Info(Clip.class);
			Line line = AudioSystem.getLine(linfo);
			Clip clip = (Clip) line;
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
			clip.open(ais);
			clip.start();
		}
		catch (Throwable t) {
			//LogManager.error(SoundPlayer.class, t);
		}
	}

	private SoundPlayer() {}
}

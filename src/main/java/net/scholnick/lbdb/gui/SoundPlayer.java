package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.ApplicationException;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * SoundPlayer is a utility class to play sound files.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
final class SoundPlayer {
    /** Play the "aaaaahhhhh.aif" sound file in a separate thread. */
    static void playAhh() {
        new Thread(() -> playFile(SoundPlayer.class.getClassLoader().getResource("aaaaahhhhh.aif"))).start();
    }

    /** Play the "boing.aif" sound file in a separate thread. */
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
            throw new ApplicationException("Unable to play audio", t);
        }
    }

    private SoundPlayer() {}
}

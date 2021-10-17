package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.ApplicationException;

import javax.sound.sampled.*;
import java.net.URL;

// TOOO: convert this to a spring class and use annotation to spin the thread
final class SoundPlayer {
    static void playAhh() {
        new Thread(() -> playFile(SoundPlayer.class.getClassLoader().getResource("aaaaahhhhh.aif"))).start();
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
            throw new ApplicationException("Unable to play audio", t);
        }
    }

    private SoundPlayer() {}
}

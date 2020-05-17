package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.domain.ApplicationException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import java.net.URL;

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

    private SoundPlayer() {
    }
}

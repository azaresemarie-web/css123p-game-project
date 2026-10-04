/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Nalla
 */
public class SoundEffects {
    private Clip clip;

    private final URL[] soundURL = new URL[10];

    public SoundEffects() {
        soundURL[0] = getClass().getResource("/sound/bgm.wav");
        soundURL[1] = getClass().getResource("/sound/right.wav");
        soundURL[2] = getClass().getResource("/sound/wrong.wav");
        soundURL[3] = getClass().getResource("/sound/timer.wav");
        soundURL[4] = getClass().getResource("/sound/ring.wav");
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] == null) {
                System.err.println("Cannot find sound file at index: " + i);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
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
    // The Clip object that holds the loaded audio in memory and plays it
    private Clip clip;

    // Array storing the file location URLs of your .wav files
    private final URL[] soundURL = new URL[10];

    public SoundEffects() {
        // Index 0: Background music (loops continuously)
        soundURL[0] = getClass().getResource("/sound/bgm.wav");

        // Index 1: Correct answer chime (when door/math puzzle is solved)
        soundURL[1] = getClass().getResource("/sound/right.wav");

        // Index 2: Wrong answer buzzer (when answer is wrong or timer runs out)
        soundURL[2] = getClass().getResource("/sound/wrong.wav");

        // Index 3: Timer tick / warning sound (during the 5-second countdown)
        soundURL[3] = getClass().getResource("/sound/timer.wav");
    }

    // Opens and prepares the chosen audio file
    public void setFile(int i) {
        try {
            if (soundURL[i] == null) {
                System.err.println("Cannot find sound file at index: " + i);
                return;
            }
            // Converts the file URL into an audio stream Java can read
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Plays the sound once from the start (for sound effects: right, wrong, timer)
    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    // Loops the sound infinitely (for bgm.wav)
    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Stops and unloads the audio line
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}

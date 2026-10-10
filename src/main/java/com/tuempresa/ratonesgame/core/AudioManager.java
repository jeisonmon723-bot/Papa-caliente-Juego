package com.tuempresa.ratonesgame.core;

import javafx.scene.media.AudioClip;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class AudioManager implements AutoCloseable {
    private static final double CHEF_HEARING_RADIUS = 340;
    private static final int SAMPLE_RATE = 22_050;

    private final List<Path> temporarySounds = new ArrayList<>();
    private final AudioClip footsteps;
    private final AudioClip chaseMusic;
    private final AudioClip captureImpact;
    private double stepTimer;
    private boolean chasePlaying;

    public AudioManager() {
        footsteps = createClip("rat-steps", 0.14, AudioManager::footstepSample);
        chaseMusic = createClip("chase-loop", 2.4, AudioManager::chaseSample);
        captureImpact = createClip("capture-impact", 0.38, AudioManager::captureSample);
        if (footsteps != null) {
            footsteps.setVolume(0.7);
        }
        if (chaseMusic != null) {
            chaseMusic.setVolume(0.32);
            chaseMusic.setCycleCount(AudioClip.INDEFINITE);
        }
    }

    public void updateProximity(double distance, double deltaSeconds, boolean chasing) {
        boolean nearby = distance < CHEF_HEARING_RADIUS;
        if (nearby && footsteps != null) {
            double proximity = 1 - distance / CHEF_HEARING_RADIUS;
            double interval = 0.62 - proximity * 0.34;
            stepTimer -= deltaSeconds;
            if (stepTimer <= 0) {
                footsteps.play(0.18 + proximity * 0.72);
                stepTimer = interval;
            }
        } else {
            stepTimer = 0;
        }

        if (chasing) {
            if (chaseMusic != null && !chasePlaying) {
                chaseMusic.play();
                chasePlaying = true;
            }
            if (chaseMusic != null) {
                chaseMusic.setVolume(0.22 + Math.max(0, 1 - distance / CHEF_HEARING_RADIUS) * 0.35);
            }
        } else {
            stopChaseMusic();
        }
    }

    public void playCaptureImpact() {
        if (captureImpact != null) {
            captureImpact.play(0.9);
        }
    }

    public void reset() {
        stepTimer = 0;
        if (footsteps != null) {
            footsteps.stop();
        }
        if (captureImpact != null) {
            captureImpact.stop();
        }
        stopChaseMusic();
    }

    @Override
    public void close() {
        stopChaseMusic();
        if (footsteps != null) {
            footsteps.stop();
        }
        if (captureImpact != null) {
            captureImpact.stop();
        }
        for (Path sound : temporarySounds) {
            try {
                Files.deleteIfExists(sound);
            } catch (IOException ignored) {
                sound.toFile().deleteOnExit();
            }
        }
    }

    private void stopChaseMusic() {
        if (chaseMusic != null && chasePlaying) {
            chaseMusic.stop();
            chasePlaying = false;
        }
    }

    private AudioClip createClip(String prefix, double seconds, DoubleUnaryOperator waveform) {
        int sampleCount = (int) (SAMPLE_RATE * seconds);
        byte[] pcm = new byte[sampleCount * 2];
        for (int index = 0; index < sampleCount; index++) {
            double time = (double) index / SAMPLE_RATE;
            double sample = Math.max(-1, Math.min(1, waveform.applyAsDouble(time)));
            short value = (short) (sample * Short.MAX_VALUE);
            pcm[index * 2] = (byte) value;
            pcm[index * 2 + 1] = (byte) (value >>> 8);
        }

        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        try {
            Path file = Files.createTempFile(prefix, ".wav");
            try (AudioInputStream audio = new AudioInputStream(
                    new ByteArrayInputStream(pcm), format, sampleCount)) {
                AudioSystem.write(audio, AudioFileFormat.Type.WAVE, file.toFile());
            }
            temporarySounds.add(file);
            return new AudioClip(file.toUri().toString());
        } catch (IOException | RuntimeException exception) {
            System.err.println("Audio disabled for " + prefix + ": " + exception.getMessage());
            return null;
        }
    }

    private static double footstepSample(double time) {
        double pulse = Math.max(pulseEnvelope(time, 0.015, 0.055), pulseEnvelope(time, 0.075, 0.12));
        double click = Math.sin(2 * Math.PI * 92 * time) * 0.55
                + Math.sin(2 * Math.PI * 183 * time) * 0.22;
        return pulse * click * 0.48;
    }

    private static double chaseSample(double time) {
        double beat = 0.55 + 0.45 * Math.sin(2 * Math.PI * 1.7 * time);
        double bass = Math.sin(2 * Math.PI * 73.4 * time) * 0.38;
        double note = Math.sin(2 * Math.PI * 110 * time) * 0.18
                + Math.sin(2 * Math.PI * 146.8 * time) * 0.12;
        return (bass + note) * beat * 0.22;
    }

    private static double captureSample(double time) {
        double envelope = Math.exp(-9 * time);
        return envelope * (Math.sin(2 * Math.PI * 58 * time) * 0.58
                + Math.sin(2 * Math.PI * 116 * time) * 0.18);
    }

    private static double pulseEnvelope(double time, double start, double end) {
        if (time < start || time > end) {
            return 0;
        }
        double progress = (time - start) / (end - start);
        return Math.sin(Math.PI * progress) * Math.exp(-2.7 * progress);
    }
}
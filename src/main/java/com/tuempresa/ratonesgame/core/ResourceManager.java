package com.tuempresa.ratonesgame.core;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class ResourceManager {
    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, AudioClip> sounds = new HashMap<>();

    public Image loadImage(String resourcePath) {
        return images.computeIfAbsent(resourcePath, this::readImage);
    }

    public AudioClip loadSound(String resourcePath) {
        return sounds.computeIfAbsent(resourcePath, path -> {
            URL resource = ResourceManager.class.getResource(path);
            if (resource == null) {
                throw new IllegalArgumentException("Sound resource not found: " + path);
            }
            return new AudioClip(resource.toExternalForm());
        });
    }

    private Image readImage(String resourcePath) {
        try (InputStream input = ResourceManager.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Image resource not found: " + resourcePath);
            }
            if (!resourcePath.toLowerCase().endsWith(".svg")) {
                return new Image(input);
            }

            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
            return new Image(new ByteArrayInputStream(output.toByteArray()));
        } catch (IOException | TranscoderException exception) {
            throw new IllegalStateException("Could not load image: " + resourcePath, exception);
        }
    }
}
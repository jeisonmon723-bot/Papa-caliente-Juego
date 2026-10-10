package com.tuempresa.ratonesgame.core;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

            BufferedImage rasterized = rasterizeSvg(input);
            WritableImage image = new WritableImage(rasterized.getWidth(), rasterized.getHeight());
            PixelWriter pixels = image.getPixelWriter();
            for (int y = 0; y < rasterized.getHeight(); y++) {
                for (int x = 0; x < rasterized.getWidth(); x++) {
                    pixels.setArgb(x, y, rasterized.getRGB(x, y));
                }
            }
            return image;
        } catch (IOException | TranscoderException exception) {
            throw new IllegalStateException("Could not load image: " + resourcePath, exception);
        }
    }

    private BufferedImage rasterizeSvg(InputStream input) throws TranscoderException {
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.transcode(new TranscoderInput(input), (TranscoderOutput) null);
        if (transcoder.getImage() == null) {
            throw new IllegalStateException("SVG transcoder produced no image");
        }
        return transcoder.getImage();
    }

    private static final class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage image;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage image, TranscoderOutput output) {
            this.image = image;
        }

        public BufferedImage getImage() {
            return image;
        }
    }
}
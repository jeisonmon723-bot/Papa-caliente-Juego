package com.tuempresa.ratonesgame.view;

import com.tuempresa.ratonesgame.core.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public final class LightingSystem {
    private static final List<LightSource> LIGHTS = List.of(
            new LightSource(260, 180, 190, Color.rgb(255, 218, 148, 0.52)),
            new LightSource(760, 300, 210, Color.rgb(255, 225, 169, 0.48)),
            new LightSource(1250, 280, 220, Color.rgb(255, 216, 142, 0.5)),
            new LightSource(1650, 650, 280, Color.rgb(255, 137, 74, 0.58)),
            new LightSource(1500, 900, 125, Color.rgb(174, 191, 207, 0.19)));

    public void render(GraphicsContext graphics, double width, double height,
                       Camera camera, boolean playerHidden) {
        graphics.save();
        graphics.setFill(Color.rgb(12, 15, 23, playerHidden ? 0.68 : 0.34));
        graphics.fillRect(0, 0, width, height);

        for (LightSource light : LIGHTS) {
            double screenX = light.worldX - camera.getOffsetX();
            double screenY = light.worldY - camera.getOffsetY();
            if (screenX + light.radius < 0 || screenY + light.radius < 0
                    || screenX - light.radius > width || screenY - light.radius > height) {
                continue;
            }
            RadialGradient glow = new RadialGradient(0, 0, screenX, screenY,
                    light.radius, false, CycleMethod.NO_CYCLE,
                    new Stop(0, light.color),
                    new Stop(0.45, Color.color(light.color.getRed(), light.color.getGreen(),
                            light.color.getBlue(), light.color.getOpacity() * 0.34)),
                    new Stop(1, Color.TRANSPARENT));
            graphics.setFill(glow);
            graphics.fillOval(screenX - light.radius, screenY - light.radius,
                    light.radius * 2, light.radius * 2);
        }
        graphics.restore();
    }

    private record LightSource(double worldX, double worldY, double radius, Color color) {
    }
}
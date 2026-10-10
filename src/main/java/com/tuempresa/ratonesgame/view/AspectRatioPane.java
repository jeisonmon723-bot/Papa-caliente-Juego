package com.tuempresa.ratonesgame.view;

import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

public final class AspectRatioPane extends Region {
    private final Region content;
    private final double contentWidth;
    private final double contentHeight;
    private final Scale scale = new Scale(1, 1, 0, 0);

    public AspectRatioPane(Region content, double contentWidth, double contentHeight) {
        if (contentWidth <= 0 || contentHeight <= 0) {
            throw new IllegalArgumentException("Content dimensions must be positive");
        }
        this.content = content;
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;

        content.setMinSize(contentWidth, contentHeight);
        content.setPrefSize(contentWidth, contentHeight);
        content.setMaxSize(contentWidth, contentHeight);
        content.getTransforms().add(scale);
        getChildren().add(content);

        setMinSize(0, 0);
        setPrefSize(contentWidth, contentHeight);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    protected void layoutChildren() {
        double factor = Math.min(getWidth() / contentWidth, getHeight() / contentHeight);
        if (!Double.isFinite(factor) || factor <= 0) {
            return;
        }

        scale.setX(factor);
        scale.setY(factor);
        content.resize(contentWidth, contentHeight);
        content.relocate(
                (getWidth() - contentWidth * factor) / 2,
                (getHeight() - contentHeight * factor) / 2);
        content.layout();
    }
}

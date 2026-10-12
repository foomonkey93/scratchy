package io.choigus;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportFrameBuffer extends FrameBuffer {
    private final Viewport viewport;

    public ViewportFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, Viewport viewport) {
        super(format, width, height, hasDepth);
        this.viewport = viewport;
    }

    @Override
    public void end() {
        super.end();
        if (viewport != null) {
            viewport.apply();
        }
    }
}

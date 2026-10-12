package io.choigus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import io.choigus.helpers.Utils;

import java.nio.IntBuffer;

public class ScratchableTexture {
    private Texture scratchTexture;
    private ViewportFrameBuffer frameBuffer;
    private String scratchLayerImagePath;
    private final Array<Vector2> scratchPoints = new Array<>();
    Engine game;
    private final Vector2 tempTouch = new Vector2();

    public ScratchableTexture(String scratchLayerImagePath, Texture scratchTexture, Engine game) {
        this.scratchLayerImagePath = scratchLayerImagePath;
        this.scratchTexture = scratchTexture;
        this.game = game;
    }

    public void generateScratchBits() {
        Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBuffer.begin();
        generateScratchLayer(game);
        generateHole();
        frameBuffer.end();
    }

    public record BufferData(
        int previousSrcRgb,
        int previousDstRgb,
        int previousSrcAlpha,
        int previousDstAlpha,
        boolean blendingEnabled) {
    }

    public void generateScratchLayer(Engine engine) {
        engine.batch.begin();
        Vector2 center = game.getCenter();
        Vector2 textureCenter = Utils.center(scratchTexture);
        engine.batch.draw(scratchTexture, center.x - textureCenter.x, center.y - textureCenter.y);
        engine.batch.end();
    }

    public void generateHole() {
        BufferData data = handleBlendingData();
        try {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
            addToScratchPoints();

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Vector2 point : scratchPoints) {
                game.shapeRenderer.circle(point.x, point.y, 25f);
            }
            game.shapeRenderer.end();

        } finally {
            resetBlendingData(data);
        }
    }

    private void addToScratchPoints() {
        if (Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            tempTouch.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(tempTouch);
            scratchPoints.add(new Vector2(tempTouch.x, tempTouch.y));
        }
    }


    public void drawScratches() {
        Texture fboTex = frameBuffer.getColorBufferTexture();
        game.batch.draw(fboTex, 0, 0, game.WORLD_WIDTH, game.WORLD_HEIGHT, 0, 0, 1, 1);
    }

    //retain previous state so it can be reset after
    public BufferData handleBlendingData() {
        boolean blendingEnabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        IntBuffer buffer = BufferUtils.newIntBuffer(1);

        Gdx.gl.glGetIntegerv(GL20.GL_BLEND_SRC_RGB, buffer);
        int previousSrc = buffer.get(0);

        Gdx.gl.glGetIntegerv(GL20.GL_BLEND_DST_RGB, buffer);
        int previousDst = buffer.get(0);

        Gdx.gl.glGetIntegerv(GL20.GL_BLEND_SRC_ALPHA, buffer);
        int previousSrcAlpha = buffer.get(0);

        Gdx.gl.glGetIntegerv(GL20.GL_BLEND_DST_ALPHA, buffer);
        int previousDstAlpha = buffer.get(0);

        return new BufferData(previousSrc, previousDst, previousSrcAlpha, previousDstAlpha, blendingEnabled);
    }

    public void resetBlendingData(BufferData data) {
        Gdx.gl.glBlendFuncSeparate(
            data.previousSrcRgb(),
            data.previousDstRgb(),
            data.previousSrcAlpha(),
            data.previousDstAlpha()
        );

        if (data.blendingEnabled()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void onResize() {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = new ViewportFrameBuffer(Pixmap.Format.RGBA8888, (int) game.WORLD_WIDTH, (int) game.WORLD_HEIGHT, false, game.viewport);
    }

    public void onDispose() {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
    }

    public void onClear() {
        scratchPoints.clear();
    }
}

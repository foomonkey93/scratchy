package io.choigus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import io.choigus.Engine;
import io.choigus.Tile;
import io.choigus.ViewportFrameBuffer;
import io.choigus.helpers.Utils;
import io.choigus.screens.types.FreshScreen;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends FreshScreen {

    private Texture background;
    private Texture backgroundScratch;

    double max = 10;
    double min = 0;
    private ArrayList<Tile> tiles = new ArrayList<>();
    int sizeX = 8;
    int sizeY = 8;
    double[][] grid = new double[sizeX][sizeY];
    ArrayList<Point> pointsUnused = new ArrayList<>();
    Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout();
    ViewportFrameBuffer frameBuffer;


    private final Array<Vector2> scratchPoints = new Array<>();
    private final Vector2 tempTouch = new Vector2();
    private final String backgroundImagePath;
    private final String scratchLayerImagePath;

    public GameScreen(Engine game, String backgroundImagePath, String scratchLayerImagePath) {
        super(game);
        this.backgroundImagePath = backgroundImagePath;
        this.scratchLayerImagePath = scratchLayerImagePath;
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);
        reScratchy();
    }

    @Override
    protected void onRender(float delta) {
        doGameBits(game);
    }

    private void addToScratchPoints() {
        if (Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            tempTouch.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(tempTouch);
            scratchPoints.add(new Vector2(tempTouch.x, tempTouch.y));
        }
    }

    public void cleanupBoard() {
        tiles.clear();
        pointsUnused.clear();
        scratchPoints.clear();
        grid = new double[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                pointsUnused.add(new Point(x + 1, y + 1));
            }
        }
    }

    public void generateTiles() {
        for (int i = 0; i < (sizeX * sizeY); i++) {
            double amt = Math.round((min + (max - min) * Math.random()) * 100.0) / 100.0;
            int randomIndex = random.nextInt(pointsUnused.size());
            Point p = pointsUnused.remove(randomIndex);
            tiles.add(new Tile(amt, p));
            grid[p.x - 1][p.y - 1] = amt;
        }
    }


    public void reScratchy() {
        cleanupBoard();
        generateTiles();
    }

    public void generateNubbyScratch(Engine engine) {
        engine.batch.begin();
        Vector2 center = game.getCenter();
        Vector2 textureCenter = Utils.center(backgroundScratch);
        engine.batch.draw(backgroundScratch, center.x - textureCenter.x, center.y - textureCenter.y);
        engine.batch.end();
    }

    public record BufferData(
        int previousSrcRgb,
        int previousDstRgb,
        int previousSrcAlpha,
        int previousDstAlpha,
        boolean blendingEnabled) {
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

    public void generateScratchBits(Engine engine) {
        Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBuffer.begin();
        generateNubbyScratch(engine);
        generateHole();
        frameBuffer.end();
    }

    public void doGameBits(Engine engine) {
        generateScratchBits(engine);
        engine.batch.begin();
        drawBackground(engine);
        drawTiles(engine);
        drawScratches(engine);
        engine.batch.end();

    }

    public void drawScratches(Engine engine) {
        Texture fboTex = frameBuffer.getColorBufferTexture();
        engine.batch.draw(fboTex, 0, 0, game.WORLD_WIDTH, game.WORLD_HEIGHT, 0, 0, 1, 1);
    }


    public void drawBackground(Engine engine) {
        Vector2 center = game.getCenter();
        Vector2 textureCenter = Utils.center(background);
        engine.batch.draw(background, center.x - textureCenter.x, center.y - textureCenter.y);
    }

    public void drawTiles(Engine engine) {
        float xSize = game.WORLD_WIDTH / sizeX;
        float ySize = game.WORLD_HEIGHT / sizeY;
        for (Tile tile : tiles) {
            float leftX = (tile.point.x - 1) * xSize;
            float bottomY = (tile.point.y - 1) * ySize;
            layout.setText(game.font, String.valueOf(tile.amt));
            game.font.draw(engine.batch, String.valueOf(tile.amt), leftX + (xSize - layout.width) / 2, bottomY + (ySize + layout.height) / 2);
        }
    }


    @Override
    public void show() {
        game.assetManager.load(backgroundImagePath, Texture.class);
        game.assetManager.load(scratchLayerImagePath, Texture.class);

        game.assetManager.finishLoading();
        background = game.assetManager.get(backgroundImagePath, Texture.class);
        backgroundScratch = game.assetManager.get(scratchLayerImagePath, Texture.class);
    }


    @Override
    public void dispose() {
        if (game.assetManager.isLoaded(backgroundImagePath)) {
            game.assetManager.unload(backgroundImagePath);
        }
        if (game.assetManager.isLoaded(scratchLayerImagePath)) {
            game.assetManager.unload(scratchLayerImagePath);
        }
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
    }

    @Override
    public void hide() {
        dispose();
    }


    @Override
    public void resize(int width, int height) {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }
        frameBuffer = new ViewportFrameBuffer(Pixmap.Format.RGBA8888, (int) game.WORLD_WIDTH, (int) game.WORLD_HEIGHT, false, game.viewport);

    }

}

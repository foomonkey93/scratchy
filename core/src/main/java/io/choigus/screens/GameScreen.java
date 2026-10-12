package io.choigus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.choigus.Engine;
import io.choigus.Tile;
import io.choigus.ViewportFrameBuffer;
import io.choigus.helpers.Utils;
import io.choigus.screens.types.FreshScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends FreshScreen {

    private Texture background;
    private Texture backgroundScratch;

    double max = 10;
    double min = 0;
    private ArrayList<Tile> tiles = new ArrayList<>();
    BitmapFont font = new BitmapFont();
    int sizeX = 8;
    int sizeY = 8;
    double[][] grid = new double[sizeX][sizeY];
    ArrayList<Point> pointsUnused = new ArrayList<>();
    Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout();
    FrameBuffer frameBuffer;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();


    private Array<Vector2> scratchPoints = new Array<>();
    private Vector2 tempTouch = new Vector2(); // Temporary vector to avoid memory allocation

    public GameScreen(Engine game) {
        super(game);
        reScratchy();
    }

    @Override
    protected void onRender(float delta) {
        game().viewport.apply();
        doGameBits(game());
    }

    private void addToScratchPoints() {
        if (Gdx.input.isTouched()) {
            tempTouch.set(Gdx.input.getX(), Gdx.input.getY());
            game().viewport.unproject(tempTouch);
            scratchPoints.add(new Vector2(tempTouch.x, tempTouch.y));
        }
    }


    public void reScratchy() {
        tiles.clear();
        pointsUnused.clear();
        grid = new double[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                pointsUnused.add(new Point(x + 1, y + 1));
            }
        }

        for (int i = 0; i < (sizeX * sizeY); i++) {
            double amt = Math.round((min + (max - min) * Math.random()) * 100.0) / 100.0;
            int randomIndex = random.nextInt(pointsUnused.size());
            Point p = pointsUnused.remove(randomIndex);
            tiles.add(new Tile(amt, p));
            grid[p.x - 1][p.y - 1] = amt;
        }
    }

    public void generateNubbyScratch(Engine engine) {
        engine.batch.begin();
        Vector2 center = game().getCenter();
        Vector2 textureCenter = Utils.center(background);
        engine.batch.draw(backgroundScratch, center.x - textureCenter.x, center.y - textureCenter.y);
        engine.batch.end();
    }

    public void generateHole() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(game().camera.combined);
        addToScratchPoints();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Vector2 point : scratchPoints) {
            shapeRenderer.circle(point.x, point.y, 25f);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void generateScratchBits(Engine engine) {
        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        generateNubbyScratch(engine);
        generateHole();
        frameBuffer.end();
    }

    public void doGameBits(Engine engine) {

        generateScratchBits(engine);
        engine.batch.begin();
        drawBackground(engine);
        drawTiles(engine);

        Texture fboTex = frameBuffer.getColorBufferTexture();
        engine.batch.draw(fboTex, 0, 0, game().WORLD_WIDTH, game().WORLD_HEIGHT, 0, 0, 1, 1);
        engine.batch.end();

    }


    public void drawBackground(Engine engine) {
        Vector2 center = game().getCenter();
        Vector2 textureCenter = Utils.center(background);
        engine.batch.draw(background, center.x - textureCenter.x, center.y - textureCenter.y);
    }

    public void drawTiles(Engine engine) {
        float xSize = game().WORLD_WIDTH / sizeX;
        float ySize = game().WORLD_HEIGHT / sizeY;
        for (Tile tile : tiles) {
            float leftX = (tile.point.x - 1) * xSize;
            float bottomY = (tile.point.y - 1) * ySize;
            layout.setText(font, String.valueOf(tile.amt));
            font.draw(engine.batch, String.valueOf(tile.amt), leftX + (xSize - layout.width) / 2, bottomY + (ySize + layout.height) / 2);
        }
    }


    @Override
    public void show() {
        game().assetManager.load("nubby-blush.png", Texture.class);
        game().assetManager.finishLoading();
        background = game().assetManager.get("nubby-blush.png", Texture.class);
        backgroundScratch = game().assetManager.get("nubby-blush.png", Texture.class);
    }


    @Override
    public void dispose() {
        if (game().assetManager.isLoaded("nubby-blush.png")) {
            game().assetManager.unload("nubby-blush.png");
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
        frameBuffer = new ViewportFrameBuffer(Pixmap.Format.RGBA8888, (int) game().WORLD_WIDTH, (int) game().WORLD_HEIGHT, false, game().viewport);

    }

}

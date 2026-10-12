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
import io.choigus.Engine;
import io.choigus.Tile;

import io.choigus.helpers.Utils;
import io.choigus.screens.types.FreshScreen;


import java.awt.*;
import java.awt.geom.Point2D;
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
    private ShapeRenderer shapeRenderer;

    private Vector2 lastTouch = new Vector2();
    private boolean isScratched = false;

    public GameScreen(Engine game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        reScratchy();
    }

    @Override
    protected void onRender(float delta) {
        doGameBits(game());
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

    public void drawNubbyScratch(Engine engine){
        engine.batch.begin();
        Point2D.Float center=game().getCenter();
        Point2D.Float textureCenter= Utils.center(background);
        System.out.println("center in nub "+(center.x-textureCenter.x) + " "+ (center.y-textureCenter.y));
        engine.batch.draw(backgroundScratch, center.x-textureCenter.x, center.y-textureCenter.y);
        engine.batch.end();
    }

    public void drawHole(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(game().camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.circle(300, 400, 100);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    public void doGameBits(Engine engine) {

        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawNubbyScratch(engine);
        drawHole();
        frameBuffer.end();
        engine.viewport.apply();
        engine.batch.setProjectionMatrix(engine.viewport.getCamera().combined);

        engine.batch.begin();
        drawBackground(engine);
        drawTiles(engine);


        Texture fboTex = frameBuffer.getColorBufferTexture();
        engine.batch.draw(fboTex, 0, 0, game().WORLD_WIDTH, game().WORLD_HEIGHT, 0, 0, 1, 1);
        engine.batch.end();

    }


    public void drawBackground(Engine engine){
        Point2D.Float center=game().getCenter();
        Point2D.Float textureCenter= Utils.center(background);
        System.out.println("center out nub "+(center.x-textureCenter.x) + " "+ (center.y-textureCenter.y));

        engine.batch.draw(background, center.x-textureCenter.x, center.y-textureCenter.y);
    }

    public void drawTiles(Engine engine){
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
        backgroundScratch=game().assetManager.get("nubby-blush.png", Texture.class);
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
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game().viewport.getScreenWidth(), game().viewport.getScreenHeight(), false);

    }

}

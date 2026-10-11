package io.choigus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.choigus.Engine;
import io.choigus.Tile;

import io.choigus.screens.types.FreshScreen;


import java.awt.*;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class GameScreen extends FreshScreen {

    private Texture background;
    double max = 10;
    double min = 0;
    private ArrayList<Tile> tiles = new ArrayList<>();
    BitmapFont font = new BitmapFont();
    int sizeX = 5;
    int sizeY = 5;
    double[][] grid = new double[5][5];
    ArrayList<Point> pointsUnused = new ArrayList<>();
    Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout();

    public GameScreen(Engine game) {
        super(game);
        reScratchy();
    }

    @Override
    protected void onRender(float delta) {
        doGameBits(game());

        if (Gdx.input.justTouched()) {
            game().setScreen(game().menuScreen);
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

    public void doGameBits(Engine engine) {
        engine.batch.begin();
        engine.batch.draw(background, 0, 0);
        float xSize = game().WORLD_WIDTH / sizeX;
        float ySize = game().WORLD_HEIGHT / sizeY;
        for (Tile tile : tiles) {
            float leftX = (tile.point.x - 1) * xSize;
            float bottomY = (tile.point.y - 1) * ySize;
            layout.setText(font, String.valueOf(tile.amt));
            font.draw(engine.batch, String.valueOf(tile.amt), leftX + (xSize - layout.width) / 2, bottomY + (ySize + layout.height) / 2);
        }
        engine.batch.end();
    }


    @Override
    public void show() {
        game().assetManager.load("nubby-blush.png", Texture.class);
        game().assetManager.finishLoading();
        background = game().assetManager.get("nubby-blush.png", Texture.class);
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
}

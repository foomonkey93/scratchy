package io.choigus.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import io.choigus.Engine;
import io.choigus.ScratchableTexture;
import io.choigus.Tile;
import io.choigus.helpers.Utils;
import io.choigus.screens.types.FreshScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ScratchOffScreen extends FreshScreen {

    private final Texture backgroundTexture;
    private final ScratchableTexture scratchTexture;

    double max = 10;
    double min = 0;
    private ArrayList<Tile> tiles = new ArrayList<>();
    int sizeX = 8;
    int sizeY = 8;
    double[][] grid = new double[sizeX][sizeY];
    ArrayList<Point> pointsUnused = new ArrayList<>();
    Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout();

    private final String backgroundImagePath;
    private final String scratchLayerImagePath;

    public ScratchOffScreen(Engine game, String backgroundImagePath, String scratchLayerImagePath) {
        super(game);
        this.backgroundImagePath = backgroundImagePath;
        this.scratchLayerImagePath = scratchLayerImagePath;
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);

        game.assetManager.load(backgroundImagePath, Texture.class);
        game.assetManager.load(scratchLayerImagePath, Texture.class);

        game.assetManager.finishLoading();

        backgroundTexture = game.assetManager.get(backgroundImagePath, Texture.class);
        scratchTexture = new ScratchableTexture(scratchLayerImagePath, game.assetManager.get(scratchLayerImagePath, Texture.class), game);
        reScratchy();
    }

    @Override
    protected void onRender(float delta) {
        doGameBits(game);
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

    public void cleanupBoard() {
        tiles.clear();
        pointsUnused.clear();
        scratchTexture.onClear();
        grid = new double[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                pointsUnused.add(new Point(x + 1, y + 1));
            }
        }
    }


    public void doGameBits(Engine engine) {
        scratchTexture.generateScratchBits();

        engine.batch.begin();
        drawBackground(engine);
        drawTiles(engine);
        scratchTexture.drawScratches();
        engine.batch.end();
    }


    public void drawBackground(Engine engine) {
        Vector2 center = game.getCenter();
        Vector2 textureCenter = Utils.center(backgroundTexture);
        engine.batch.draw(backgroundTexture, center.x - textureCenter.x, center.y - textureCenter.y);
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
    public void dispose() {

        if (scratchTexture != null) {
            scratchTexture.onDispose();
        }
        if (game.assetManager.isLoaded(backgroundImagePath)) {
            game.assetManager.unload(backgroundImagePath);
        }
        if (game.assetManager.isLoaded(scratchLayerImagePath)) {
            game.assetManager.unload(scratchLayerImagePath);
        }

    }


    @Override
    public void resize(int width, int height) {
        scratchTexture.onResize();
    }

}

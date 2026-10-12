package io.choigus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.choigus.screens.GameScreen;
import io.choigus.screens.MenuScreen;

import java.awt.*;
import java.awt.geom.Point2D;


public class Engine extends Game {
    public SpriteBatch batch;
    public AssetManager assetManager;

    public MenuScreen menuScreen;
    public GameScreen gameScreen;

    public OrthographicCamera camera;
    public Viewport viewport;

    public float WORLD_WIDTH=800;
    public float WORLD_HEIGHT=640;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        viewport.apply(true);

        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);

        setScreen(menuScreen);

    }

    public Vector2 getCenter(){
        return new Vector2(viewport.getWorldWidth()/2,viewport.getWorldHeight()/2);
    }


    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        gameScreen.dispose();
        menuScreen.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}

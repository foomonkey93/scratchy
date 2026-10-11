package io.choigus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.choigus.screens.GameScreen;
import io.choigus.screens.MenuScreen;


public class Engine extends Game {
    public SpriteBatch batch;
    public AssetManager assetManager;

    public MenuScreen menuScreen;
    public GameScreen gameScreen;

    public OrthographicCamera camera;
    public Viewport viewport;

    public float WORLD_WIDTH=640;
    public float WORLD_HEIGHT=480;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        viewport.apply(true);
        setScreen(menuScreen);

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

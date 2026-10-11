package io.choigus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.choigus.screens.GameScreen;
import io.choigus.screens.MenuScreen;


public class Engine extends Game {
    public SpriteBatch batch;
    public AssetManager assetManager;

    public MenuScreen menuScreen;
    public GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(menuScreen);
    }


    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
        gameScreen.dispose();
        menuScreen.dispose();
    }
}

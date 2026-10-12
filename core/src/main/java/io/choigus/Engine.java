package io.choigus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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

    public ShapeRenderer shapeRenderer ;
    public BitmapFont font;

    public float WORLD_WIDTH = 800;
    public float WORLD_HEIGHT = 640;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer= new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        viewport.apply(true);

        menuScreen = new MenuScreen(this,"nubby-blush.png","Right click or suffer");
        gameScreen = new GameScreen(this,"nubby-blush.png","test.jpg");

        setScreen(menuScreen);

    }

    public Vector2 getCenter() {
        return new Vector2(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
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
        super.resize(width, height);
    }
}

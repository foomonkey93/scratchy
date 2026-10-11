package io.choigus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.choigus.interfaces.Screen;

public class GameScreen extends Screen {

    private Texture background;

    public GameScreen(Engine game) {
        super(game);
    }

    @Override
    protected void onRender(float delta) {
        doGameBits(game());

        if (Gdx.input.justTouched()) {
            game().setScreen(game().menuScreen);
        }
    }

    public void doGameBits(Engine engine){
        engine.batch.begin();
        engine.batch.draw(background, 0, 0);
        engine.batch.end();


    }


    @Override
    public void show() {
        game().assetManager.load("test.jpg", Texture.class);
        game().assetManager.finishLoading();
        background = game().assetManager.get("test.jpg", Texture.class);

    }


    @Override
    public void dispose() {
        if (game().assetManager.isLoaded("test.jpg")) {
            game().assetManager.unload("test.jpg");
        }
    }

    @Override
    public void hide() {
        dispose();
    }
}

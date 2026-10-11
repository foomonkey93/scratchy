package io.choigus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.choigus.interfaces.Screen;

public class MenuScreen extends Screen {
    private Texture background;

    public MenuScreen(Engine game) {
        super(game);
    }

    @Override
    protected void onRender(float delta) {

        game().batch.begin();
        game().batch.draw(background, 0, 0);
        game().batch.end();
        if (Gdx.input.justTouched()) {
            System.out.println("h");
            game().setScreen(game().gameScreen);
        }
    }

    @Override
    public void show () {
        game().assetManager.load("nubby-blush.png", Texture.class);

        // Block until it's loaded (or loop update() in render for a loading bar)
        game().assetManager.finishLoading();
        background = game().assetManager.get("nubby-blush.png", Texture.class);

    }



    @Override
    public void dispose () {
        if ( game().assetManager.isLoaded("nubby-blush.png")) {
            game().assetManager.unload("nubby-blush.png");
        }

    }

    @Override
    public void hide () {
        dispose();
    }
}

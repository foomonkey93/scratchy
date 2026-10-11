package io.choigus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.choigus.Engine;
import io.choigus.screens.types.FreshScreen;


public class MenuScreen extends FreshScreen {
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
            game().setScreen(game().gameScreen);
        }
    }

    @Override
    public void show () {
        game().assetManager.load("nubby-blush.png", Texture.class);
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

package io.choigus.screens.types;

import com.badlogic.gdx.utils.ScreenUtils;
import io.choigus.Engine;

/**
 * Type of screen that wipes the background on swap
 */
public abstract class FreshScreen extends Screen {
    public FreshScreen(Engine game) {
        super(game);
    }

    protected abstract void onRender(float delta);

    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);
        onRender(delta);
    }


}

package io.choigus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import io.choigus.Engine;
import io.choigus.helpers.Utils;
import io.choigus.screens.types.FreshScreen;


public class MenuScreen extends FreshScreen {
    private Texture background;
    private final GlyphLayout layout = new GlyphLayout();
    public String menuText="Right click or suffer";
    private final String backgroundImagePath;

    public MenuScreen(Engine game,String backgroundImagePath,String menuText) {
        super(game);
        this.menuText=menuText;
        this.backgroundImagePath=backgroundImagePath;
    }

    @Override
    protected void onRender(float delta) {
        game().batch.begin();
        Vector2 center = game.getCenter();
        Vector2 textureCenter = Utils.center(background);
        game.batch.draw(background, center.x - textureCenter.x, center.y - textureCenter.y);

        layout.setText(game.font, menuText);
        game.font.draw(game.batch, menuText, center.x - (layout.width) / 2, center.y - ( layout.height) / 2);

        game().batch.end();

        if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            game().setScreen(game().gameScreen);
        }
    }



    @Override
    public void show() {
        game().assetManager.load(this.backgroundImagePath, Texture.class);
        game().assetManager.finishLoading();
        background = game().assetManager.get(this.backgroundImagePath, Texture.class);

    }


    @Override
    public void dispose() {
        if (game().assetManager.isLoaded(this.backgroundImagePath)) {
            game().assetManager.unload(this.backgroundImagePath);
        }

    }

    @Override
    public void hide() {
        dispose();
    }
}

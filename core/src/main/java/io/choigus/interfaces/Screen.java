package io.choigus.interfaces;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import io.choigus.Engine;

public abstract class Screen extends ScreenAdapter {
    Engine game;
    public Screen(Engine game){
        this.game=game;
    }

    public Engine game(){
        return game;
    }

    protected abstract void onRender(float delta);

    public void render(float delta){
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        onRender(delta);
    }


}

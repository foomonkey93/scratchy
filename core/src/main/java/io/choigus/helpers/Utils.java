package io.choigus.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.awt.*;
import java.awt.geom.Point2D;

public class Utils {


    public static Point2D.Float center(Texture t){
        return new Point2D.Float((Gdx.graphics.getWidth() - t.getWidth() / 2f),(Gdx.graphics.getHeight() - t.getHeight() / 2f));
    }

    public static Point2D.Float center(float width, float height,Texture t){
        return new Point2D.Float((width - t.getWidth() / 2f),(height - t.getHeight() / 2f));
    }
}

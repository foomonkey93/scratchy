package io.choigus.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Utils {


    /**
     * Will return the exact center coord of the height and width of the texture
     *
     * @param t
     * @return
     */
    public static Vector2 center(Texture t) {
        return new Vector2((t.getWidth() / 2f), (t.getHeight() / 2f));
    }

    /**
     * Will return the exact center coord of the height and width of the texture in comparison to the x and y provided
     *
     * @param t
     * @return
     */
    public static Vector2 center(float width, float height, Texture t) {
        return new Vector2((width - t.getWidth() / 2f), (height - t.getHeight() / 2f));
    }
}

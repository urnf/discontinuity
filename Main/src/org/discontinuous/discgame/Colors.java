package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;

/**
 * Created by Urk on 4/2/14.
 */
public class Colors {
    public static final HashMap<String, Color> ColorMap = new HashMap<String, Color>() {{
        put("dark_grey", new Color(0.15f, 0.15f, 0.15f, 1));
        put("light_grey", new Color(0.8f, 0.8f, 0.8f, 1));
    }};
}

package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;

/**
 * Created by Urk on 4/2/14.
 */
public class Colors {
    static float logical_r = 34f/255;
    static float logical_g = 166f/255;
    static float logical_b = 129f/255;

    static float ethical_r = 26f/255;
    static float ethical_g = 55f/255;
    static float ethical_b = 185f/255;

    static float interrogate_r = 228f/255;
    static float interrogate_g = 144f/255;
    static float interrogate_b = 54f/255;

    static float intimidate_r = 146f/255;
    static float intimidate_g = 10f/255;
    static float intimidate_b = 20f/255;


    public static final HashMap<String, Color> ColorMap = new HashMap<String, Color>() {{
        put("dark_grey", new Color(0.15f, 0.15f, 0.15f, 1));
        put("light_grey", new Color(0.8f, 0.8f, 0.8f, 1));

        put ("consumed", new Color(67f / 255, 67f / 255, 67f / 255, 1));

        put("logical_color", new Color(logical_r, logical_g, logical_b, 1));
        put("ethical_color", new Color(ethical_r, ethical_g, ethical_b, 1));
        put("interrogate_color", new Color(interrogate_r, interrogate_g, interrogate_b, 1));
        put("intimidate_color", new Color(intimidate_r, intimidate_g, intimidate_b, 1));

        put("logical_fade", new Color(logical_r * 0.7f, logical_g * 0.7f, logical_b * 0.7f, 1));
        put("ethical_fade", new Color(ethical_r * 0.7f, ethical_g * 0.7f, ethical_b * 0.7f, 1));
        put("interrogate_fade", new Color(interrogate_r * 0.7f, interrogate_g * 0.7f, interrogate_b * 0.7f, 1));
        put("intimidate_fade", new Color(intimidate_r * 0.7f, intimidate_g * 0.7f, intimidate_b * 0.7f, 1));

        put("logical_extra_fade", new Color(logical_r * 0.3f, logical_g * 0.3f, logical_b * 0.3f, 1));
        put("ethical_extra_fade", new Color(ethical_r * 0.3f, ethical_g * 0.3f, ethical_b * 0.3f, 1));
        put("interrogate_extra_fade", new Color(interrogate_r * 0.3f, interrogate_g * 0.3f, interrogate_b * 0.3f, 1));
        put("intimidate_extra_fade", new Color(intimidate_r * 0.3f, intimidate_g * 0.3f, intimidate_b * 0.3f, 1));
    }};
}

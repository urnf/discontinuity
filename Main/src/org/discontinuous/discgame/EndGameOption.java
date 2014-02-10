package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 2/7/14.
 */
public class EndGameOption extends Entity {
    int dp_cost;
    String option_text;
    BitmapFont font;
    static Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    static Color dark_green = new Color(0.0664f, 0.4336f, 0.1523f, 1);
    static Color dark_red = new Color(0.4336f, 0.1172f, 0.0664f, 1);

    public EndGameOption(int dp_cost, BitmapFont font, String option_text) {
        super(0, 0, 0, 0);
        this.dp_cost = dp_cost;
        this.option_text = option_text;
        this.font = font;
    }

    public void drawShapeHover(ShapeRenderer shapes) {
            // if backtracking, highlight red
            if (DiscGame.dealpower.dp < dp_cost) { shapes.setColor(dark_red); }
            // if can combo into this option, highlight green
            else if (DiscGame.dealpower.dp > dp_cost) { shapes.setColor(dark_green); }
            shapes.rect(x - 6, y - 6, width + 12, height + 12);
            shapes.setColor(dark_grey);
            shapes.rect(x, y, width, height);
    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.movestats_font.drawWrapped(batch, Integer.toString(dp_cost), x + 10, y + 75, width - 40);
        font.drawWrapped(batch, option_text, x + 70, y + 75, width - 70);
    }
}

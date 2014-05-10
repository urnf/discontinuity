package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/2/14.
 */
public class DealPower extends Entity {
    int dp;
    static int consume_penalty = 200;
    static Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    static Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);

    public DealPower () {
        super(0, SympGame.screen_height/2 - 60, 60, 30);
        dp = 0;
        SympGame.hover_list.add(this);
    }
    public void draw(SpriteBatch batch) {
        // Deal Power and Deal Power Counter
        if (dp < 0) SympGame.deal_font.setColor(1, 0, 0, 1);
        if (dp > 0) SympGame.deal_font.setColor(0, 1, 0, 1);
        width = (int) SympGame.deal_font.getBounds(String.valueOf(dp)).width;
        x =  (SympGame.screen_width - width)/2;
        SympGame.deal_font.draw(batch, String.valueOf(dp), x, y + 20);
    }

    public void update(int dp_change, boolean isPlayer, boolean isConsumed) {
        if (isConsumed) {
            // Penalty needs to be flat, otherwise the AI and to a certain extent, the player will be encouraged to find
            // an "optimal" path through consumed squares, when in fact, it should be, GTFO ASAP.
            dp = isPlayer ? (dp - consume_penalty) : (dp + consume_penalty); return;
        }
        dp = isPlayer ? (dp + dp_change) : (dp - dp_change);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(x + 50, y + 50,
                300, 110,
                x + width/2, y + height/2,
                dark_grey, light_grey, false, shapes);
    }

    public void drawHover(SpriteBatch batch) {
        SympGame.text_font.drawWrapped(batch, "Deal Power\n" +
                "This determines what you'll be able to bargain for after this debate.  " +
                "You'll gain deal power by making arguments, and lose it to opponent arguments.  " +
                "Some abilities also grant deal power.", x + 50, y + 160, 300);
    }
}

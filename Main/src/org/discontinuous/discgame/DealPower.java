package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Urk on 1/2/14.
 */
public class DealPower extends Entity {
    int dp;
    static int consume_penalty = 200;

    public DealPower () {
        super(0, DiscGame.screen_height/2 - 80, 0, 0);
        dp = 0;
    }
    public void draw(SpriteBatch batch) {
        // Deal Power and Deal Power Counter
        if (dp < 0) DiscGame.deal_font.setColor(1, 0, 0, 1);
        if (dp > 0) DiscGame.deal_font.setColor(0, 1, 0, 1);
        x = (int) (DiscGame.screen_width - DiscGame.deal_font.getBounds(String.valueOf(dp)).width)/2;
        DiscGame.deal_font.draw(batch, String.valueOf(dp), x, y);
    }

    public void update(int dp_change, boolean isPlayer, boolean isConsumed) {
        dp = isPlayer ? (dp += dp_change) : (dp -= dp_change);
        if (isConsumed) {
            // Penalty needs to be flat, otherwise the AI and to a certain extent, the player will be encouraged to find
            // an "optimal" path through consumed squares, when in fact, it should be, GTFO ASAP.
            dp -= consume_penalty;
        }
    }
}

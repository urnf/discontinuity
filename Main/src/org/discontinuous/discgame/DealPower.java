package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Urk on 1/2/14.
 */
public class DealPower extends Entity {
    int dp;
    static int consume_penalty_coeff = -3;

    public DealPower (int x, int y) {
        super(x, y, 0, 0);
        dp = 0;
    }
    public void draw(SpriteBatch batch) {
        // Deal Power and Deal Power Counter
        if (dp < 0) DiscGame.deal_font.setColor(1, 0, 0, 1);
        if (dp > 0) DiscGame.deal_font.setColor(0, 1, 0, 1);
        DiscGame.deal_font.draw(batch, String.valueOf(dp), x, y);
    }

    public void update(int dp_change, boolean isPlayer, boolean isConsumed) {
        dp = isPlayer ? (dp += dp_change) : (dp -= dp_change);
        if (isConsumed) {
            dp -= dp_change * consume_penalty_coeff;
        }
    }
}

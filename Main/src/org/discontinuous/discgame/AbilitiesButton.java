package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 2/3/14.
 */
public class AbilitiesButton extends Entity {
    int tooltip_x = 0;
    int tooltip_y = 100;
    int tooltip_width = 110;
    int tooltip_height = 10;
    Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);

    public AbilitiesButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        DiscGame.hover_list.add(this);
        DiscGame.click_list.add(this);
    }

    public void drawHover(SpriteBatch batch) {
        this.draw(batch);
        DiscGame.text_font.drawWrapped(batch, "Special Abilities", x + tooltip_x, y + tooltip_y + tooltip_height, tooltip_width);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(x + tooltip_x, y + tooltip_y,
                tooltip_width, tooltip_height,
                x + width/2, y + height,
                dark_grey, light_grey, false, shapes);
    }

    public void clickHandler() {
        // Clicking activates the ability list - goes to new game state select ability
        State.currentState = State.states.SelectAbility;
    }
}

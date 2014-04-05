package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 2/3/14.
 */
public class AbilitiesButton extends Entity {
    int tooltip_x = 0;
    int tooltip_y = 100;
    int tooltip_width = 110;
    int tooltip_height = 10;
    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

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
                x + width / 2, y + height,
                inner_color, outer_color, false, shapes);
    }

    public void clickHandler() {
        if (StateHandling.checkState(State.PostGameSelect) || StateHandling.checkState(State.PostGameDialog)) { return; }
        // Clicking activates the ability list - goes to new game state select ability
        StateHandling.currentState = State.SelectAbility;
        // Add abilities to hover and click handling
        Ability.add_ability_response();
    }
}

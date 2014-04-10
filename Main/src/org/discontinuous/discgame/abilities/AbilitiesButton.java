package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.StateHandling.State;

import java.util.ArrayList;

/**
 * Created by Urk on 2/3/14.
 */
public class AbilitiesButton extends Entity {
    int tooltip_x = 0;
    int tooltip_y = 100;
    int tooltip_width = 110;
    int tooltip_height = 10;
    BitmapFont font;
    ArrayList<Entity> hover_list;
    ArrayList<Entity> click_list;
    ArrayList<Ability> abilities;
    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

    public AbilitiesButton(int x, int y, int width, int height, ArrayList<Entity> hover_list, ArrayList<Entity> click_list, ArrayList<Ability> abilities, BitmapFont font) {
        super(x, y, width, height);
        hover_list.add(this);
        click_list.add(this);
        this.font = font;
        this.hover_list = hover_list;
        this.click_list = click_list;
        this.abilities = abilities;
    }

    public void drawHover(SpriteBatch batch) {
        this.draw(batch);
        font.drawWrapped(batch, "Special Abilities", x + tooltip_x, y + tooltip_y + tooltip_height, tooltip_width);
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
        StateHandling.setState(State.SelectAbility);
        // Add abilities to hover and click handling
        Ability.add_ability_response(hover_list, click_list, abilities);
    }
}

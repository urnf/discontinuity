package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 2/3/14.
 */
public class Ability extends Entity {
    int tooltip_x = 30;
    int tooltip_y = 100;
    int tooltip_width = 300;
    int tooltip_height = 80;
    String tooltip;
    String dialog;
    Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
    AbilityTarget.targets target;
    AbilityEffect effect;
    // Inspiration cost
    int ins_cost;
    private boolean usable;
    Contestant contestant;

    public Ability (Contestant contestant, int width, int height, int ins_cost, AbilityTarget.targets target, AbilityEffect effect, String tooltip, String dialog) {
        super(0, 0, width, height);
        DiscGame.hover_list.add(this);
        DiscGame.click_list.add(this);
        this.tooltip = tooltip;
        this.dialog = dialog;
        this.target = target;
        this.effect = effect;
        this.ins_cost = ins_cost;
        this.contestant = contestant;
    }

    public void drawHover(SpriteBatch batch) {
        String text;
        if (usable) { text = tooltip; }
        else { text = "Not enough inspiration to use!\n" + tooltip; }
        DiscGame.text_font.drawWrapped(batch, text, tooltip_x, tooltip_y + tooltip_height, tooltip_width);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(tooltip_x, tooltip_y,
                tooltip_width, tooltip_height,
                x + width/2, y + height/2,
                dark_grey, light_grey, false, shapes);
    }

    public void update_usability(int inspiration) {
        if (inspiration < ins_cost) { usable = false; }
        else { usable = true; }
    }

    public void clickHandler() {
        if (!usable) { return; }
        DiscGame.yi.ability_selected = this;
        // If ability target is self, apply effect immediately
        if (target == AbilityTarget.targets.self) {
            effect.apply_effect(contestant, null);
            State.currentState = State.states.AbilityDialog;

        }
        // If ability target is not self, go to ability targeting
        else {
            State.currentState = State.states.AbilityTargeting;
        }
    }
}

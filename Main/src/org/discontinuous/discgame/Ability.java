package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 2/3/14.
 */
public class Ability extends Entity {
    int tooltip_x = 30;
    int tooltip_y = 120;
    int tooltip_width = 300;
    int tooltip_height = 80;
    String tooltip;
    String dialog;
    Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
    Color red = new Color(1, 0, 0, 1);
    Color white = new Color(1, 1, 1, 1);
    AbilityTarget.targets target;
    AbilityEffect effect;
    // Inspiration cost
    int ins_cost;
    private boolean usable;
    Contestant contestant;

    public Ability (Contestant contestant, int width, int height, int ins_cost, AbilityTarget.targets target, AbilityEffect effect, String tooltip, String dialog) {
        super(0, 0, width, height);

        this.tooltip = tooltip;
        this.dialog = dialog;
        this.target = target;
        this.effect = effect;
        this.ins_cost = ins_cost;
        this.contestant = contestant;
    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.text_font.drawWrapped(batch, tooltip, x + tooltip_x, y + tooltip_y + tooltip_height, tooltip_width);
        if (!usable) {
            DiscGame.text_font.setColor(red);
            DiscGame.text_font.drawWrapped(batch, "Not enough inspiration for this ability!", x + tooltip_x, y + tooltip_y + tooltip_height - 70, tooltip_width);
            DiscGame.text_font.setColor(white);
        }
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(x + tooltip_x, y + tooltip_y,
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
            // SPECIAL CASE FOR TABLEFLIP WOOOOOOO
            if (tooltip.contains("Tableflip")) { dialog = "Please be careful, I am about to flip my shit.\nRargh.\n(Yi hurls the table " + ((int)(Math.random() * 100) + 10) + " meters)"; }

            effect.apply_effect(contestant, null);
            State.currentState = State.states.AbilityDialog;
        }
        // If ability target is not self, go to ability targeting
        else {
            State.currentState = State.states.AbilityTargeting;
        }
        // Remove abilities from hover and click handling
        remove_ability_response();
    }

    public static void setup_ability_display(Contestant contestant) {
        int i = 0;
        for (Ability ability : DiscGame.yi.abilities) {
            ability.x = DiscGame.screen_width/2 - 230 + i * 68;
            ability.y = 190;
            ability.img.setPosition(DiscGame.screen_width/2 - 230 + i * 68, 190);
            i++;
        }
    }
    public static void add_ability_response() {
        for (Ability ability : DiscGame.yi.abilities) {
            DiscGame.hover_list.add(ability);
            DiscGame.click_list.add(ability);
        }
    }
    public static void remove_ability_response() {
        // Remove abilities from hover and click handling
        for (Ability ability : DiscGame.yi.abilities) {
            DiscGame.hover_list.remove(ability);
            DiscGame.click_list.remove(ability);
        }
    }
}

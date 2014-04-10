package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.*;

import java.util.ArrayList;

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
    BitmapFont font;

    public Ability (Contestant contestant, BitmapFont font, int width, int height, int ins_cost, AbilityTarget.targets target, AbilityEffect effect, String tooltip, String dialog) {
        super(0, 0, width, height);

        this.tooltip = tooltip;
        this.dialog = dialog;
        this.target = target;
        this.effect = effect;
        this.ins_cost = ins_cost;
        this.contestant = contestant; //I REALLY don't like that this is coupled to contestant, but can't avoid for now
        this.font = font; //same as above
    }

    // TODO: Mixing up MVC here, Ability should remain a data model, and not have rendering code in it
    public void drawHover(SpriteBatch batch) {
        //DiscGame.text_font
        font.drawWrapped(batch, tooltip, x + tooltip_x, y + tooltip_y + tooltip_height, tooltip_width);
        if (!usable) {
            font.setColor(red);
            font.drawWrapped(batch, "Not enough inspiration for this ability!", x + tooltip_x, y + tooltip_y + tooltip_height - 70, tooltip_width);
            font.setColor(white);
        }
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(x + tooltip_x, y + tooltip_y,
                tooltip_width, tooltip_height,
                x + width / 2, y + height / 2,
                dark_grey, light_grey, false, shapes);
    }

    public void update_usability(int inspiration) {
        if (inspiration < ins_cost) { usable = false; }
        else { usable = true; }
    }

    public void clickHandler() {
        if (!usable) { return; }
        contestant.ability_click(this, effect, target, tooltip, dialog);
    }

    public static void setup_ability_display(ArrayList<Ability> abilities, int screen_width, Sprite ability_img) {
        int i = 0;
        for (Ability ability : abilities) {
            ability.x = screen_width/2 - 230 + i * 68;
            ability.y = 190;
            ability_img.setPosition(screen_width/2 - 230 + i * 68, 190);
            i++;
        }
    }
    public static void add_ability_response(ArrayList<Entity> hover_list, ArrayList<Entity> click_list, ArrayList<Ability> abilities) {
        for (Ability ability : abilities) {
            hover_list.add(ability);
            click_list.add(ability);
        }
    }
    public static void remove_ability_response(ArrayList<Entity> hover_list, ArrayList<Entity> click_list, ArrayList<Ability> abilities) {
        // Remove abilities from hover and click handling
        for (Ability ability : abilities) {
            hover_list.remove(ability);
            click_list.remove(ability);
        }
    }
}

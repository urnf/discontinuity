package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
    static Texture tintable = DiscGame.manager.get("cell/tint.png", Texture.class);
    Sprite[][] costgrid;
    int tooltip_x = 30;
    int tooltip_y = 120;
    int tooltip_width = 350;
    int tooltip_height = 120;
    String tooltip;
    String dialog;
    Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
    Color red = new Color(1, 0, 0, 1);
    Color white = new Color(1, 1, 1, 1);
    AbilityTarget.targets target;
    AbilityEffect effect;
    // Ability cost
    public int logical_cost;
    public int ethical_cost;
    public int interrogate_cost;
    public int intimidate_cost;
    private boolean usable;
    Contestant contestant;
    BitmapFont font;

    public Ability (Contestant contestant, BitmapFont font, int width, int height, int logical_cost, int ethical_cost, int interrogate_cost, int intimidate_cost, AbilityTarget.targets target, AbilityEffect effect, String tooltip, String dialog) {
        super(0, 0, width, height);

        this.tooltip = tooltip;
        this.dialog = dialog;
        this.target = target;
        this.effect = effect;
        this.logical_cost = logical_cost;
        this.ethical_cost = ethical_cost;
        this.interrogate_cost = interrogate_cost;
        this.intimidate_cost = intimidate_cost;
        this.contestant = contestant; //I REALLY don't like that this is coupled to contestant, but can't avoid for now
        this.font = font; //same as above

        costgrid = new Sprite[4][4];
        if (logical_cost > 0) {
            for (int i = 0; i < logical_cost; i++) {
                costgrid[0][i] = new Sprite(tintable, 48, 48);
                costgrid[0][i].setColor(Colors.ColorMap.get("logical_color"));
                costgrid[0][i].setScale(0.5f);
            }
        }
        if (ethical_cost > 0) {
            for (int i = 0; i < ethical_cost; i++) {
                costgrid[1][i] = new Sprite(tintable, 48, 48);
                costgrid[1][i].setColor(Colors.ColorMap.get("ethical_color"));
                costgrid[1][i].setScale(0.5f);
            }
        }
        if (interrogate_cost > 0) {
            for (int i = 0; i < interrogate_cost; i++) {
                costgrid[2][i] = new Sprite(tintable, 48, 48);
                costgrid[2][i].setColor(Colors.ColorMap.get("interrogate_color"));
                costgrid[2][i].setScale(0.5f);
            }
        }
        if (intimidate_cost > 0) {
            for (int i = 0; i < intimidate_cost; i++) {
                costgrid[3][i] = new Sprite(tintable, 48, 48);
                costgrid[3][i].setColor(Colors.ColorMap.get("intimidate_color"));
                costgrid[3][i].setScale(0.5f);
            }
        }
        /*
        for (int i = 0; i < costgrid.length; i++) {
            for (int j = 0; j < costgrid[i].length; j++) {
                if (null != costgrid[i][j]) {
                    costgrid[i][j].setPosition(x + 24 * i, 230 + 24 * j);
                }
            }
        }*/

    }

    // TODO: Mixing up MVC here, Ability should remain a data model, and not have rendering code in it
    public void drawHover(SpriteBatch batch) {
        for (int i = 0; i < costgrid.length; i++) {
            for (int j = 0; j < costgrid[i].length; j++) {
                if (null != costgrid[i][j]) costgrid[i][j].draw(batch);
            }
        }
        //DiscGame.text_font
        font.drawWrapped(batch, tooltip, x + tooltip_x, y + tooltip_y + tooltip_height, tooltip_width);
        font.draw(batch, "Cost:", 220 + x + tooltip_x, y + tooltip_y + tooltip_height);
        /*
        if (!usable) {
            font.setColor(red);
            font.drawWrapped(batch, "Not enough inspiration for this ability!", x + tooltip_x, y + tooltip_y + tooltip_height - 70, tooltip_width);
            font.setColor(white);
        }*/
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Tooltip.newTip(x + tooltip_x, y + tooltip_y,
                tooltip_width, tooltip_height,
                x + width / 2, y + height / 2,
                dark_grey, light_grey, false, shapes);
    }

    public void drawDialog(SpriteBatch batch, int width_offset, int height_offset, int width) {
        font.drawWrapped(batch, dialog, width_offset, height_offset, width);
    }

    public void update_usability(int logical_bar, int ethical_bar, int interrogate_bar, int intimidate_bar) {
        if (logical_bar < logical_cost ||
            ethical_bar < ethical_cost ||
            interrogate_bar < interrogate_cost ||
            intimidate_bar < intimidate_cost) { usable = false; }
        else { usable = true; }
    }

    public void clickHandler() {
        if (!usable) { return; }
        contestant.ability_click(this, effect, target, tooltip, dialog);
    }

    // Positions the ability list on the screen when ability button is clicked
    public static void setup_ability_display(ArrayList<Ability> abilities, int screen_width) {
        int i = 0;
        for (Ability ability : abilities) {
            ability.x = 30 + i * 68;
            ability.y = 30;
            ability.img.setPosition(ability.x, ability.y);
            i++;

            for (int j = 0; j < ability.costgrid.length; j++) {
                for (int k = 0; k < ability.costgrid[j].length; k++) {
                    if (null != ability.costgrid[j][k]) {
                        ability.costgrid[j][k].setPosition(ability.x + 280 + 24 * j, 238 + 24 * k);
                    }
                }
            }
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

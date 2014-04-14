package org.discontinuous.discgame.contestants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.Board;
import org.discontinuous.discgame.Combo;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.abilities.AbilityList;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 4/4/14.
 */
public class Confucius extends Contestant {
    static int CONF_MAX = 110;
    static int INSP_MAX = 130;

    static Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
        put("power", 60);
        put("conf_plus", 0);
        put("conf_minus", 10);
        put("ins_plus", 10);
        put("ins_minus", 0);
    }};
    static Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
        put("power", 40);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 0);
        put("ins_minus", 0);
    }};
    static Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
        put("power", 80);
        put("conf_plus", 0);
        put("conf_minus", 20);
        put("ins_plus", 0);
        put("ins_minus", 10);
    }};
    static Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
        put("power", 120);
        put("conf_plus", 30);
        put("conf_minus", 20);
        put("ins_plus", 30);
        put("ins_minus", 10);
    }};
    static ArrayList<String []> combo_list = new  ArrayList<String []>() {{
        add(new String[]{"Intimidate", "Logical"});
        add(new String[]{"Logical", "Ethical"});
        add(new String[]{"Interrogate", "Ethical"});
        add(new String[]{"Interrogate", "Logical"});
        add(new String[]{"Interrogate", "Intimidate"});
        add(new String[]{"Intimidate", "Ethical"});
    }};


    public Confucius( boolean isPlayer,
                      int board_x,
                      int board_y,
                      int screen_width,
                      BitmapFont font,
                      Cell cell) {
        super(board_x, board_y, log_stats, eth_stats, inm_stats, ing_stats, CONF_MAX, INSP_MAX, screen_width/2 - (Board.CELL_EDGE_SIZE * board_x/2) + 40, isPlayer, cell);
        // Set up Arlene's combos
        this.set_combo(new Combo(combo_list));

        Portrait portrait = new Portrait(this, new Texture(Gdx.files.internal("img/arlene-combos.png")), screen_width - 290,0, 300, 375, screen_width/2 - 250, 700, screen_width - 280, 250, 520, "Confucius\n" +
                "J.D. University of New Oxford\n" +
                "Elecantos Legal Group\n" +
                "Professor Emeritus, Harvard Mars Law Adjunct\n\n" +
                "When she's not preparing for a major case or incinerating revenant souls, she relishes dishing out " +
                "verbal suplexes upon opposing counsel or unruly law students.  " +
                "Her fearsome reputation as both sorceror and lawyer means that few people cross her, professionally or personally.");
        portrait.setImg(new Texture(Gdx.files.internal("img/arlene.png")));
        //portrait.setContestant(arlene);
        this.set_portrait(portrait);

        //arlene = new Contestant(arlene_combo, 1, 1, log_stats, eth_stats, inm_stats, ing_stats, 200, 200, screen_width/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 40, false, current_board.cells[0][0]);
        setImg(new Texture(Gdx.files.internal("img/arlenemini.png")));

        if (isPlayer) {
            // Init AbilityList abilities here
            AbilityList.init_abilities(this, get_abilities(), font);

            Ability.setup_ability_display(get_abilities(), screen_width);
        }
    }
}

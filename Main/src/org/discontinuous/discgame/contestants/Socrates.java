package org.discontinuous.discgame.contestants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.abilities.AbilityList;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 4/4/14.
 */
public class Socrates extends Contestant {
    static int LOG_MAX = 4;
    static int ETH_MAX = 4;
    static int ING_MAX = 4;
    static int INT_MAX = 4;

    // Setup Contestant stats
    static Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
        put("power", 100);
        put("conf_plus", 25);
        put("conf_minus", 0);
        put("ins_plus", 10);
        put("ins_minus", 10);
    }};
    static Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
        put("power", 30);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 0);
        put("ins_minus", 20);
    }};
    static Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
        put("power", 50);
        put("conf_plus", 10);
        put("conf_minus", 20);
        put("ins_plus", 0);
        put("ins_minus", 0);
    }};
    static Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
        put("power", 70);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 30);
        put("ins_minus", 0);
    }};

    static ArrayList<String[]> combo_list = new  ArrayList<String[]>() {{
        add(new String[]{"Intimidate", "Logical"});
        add(new String[]{"Logical", "Ethical"});
        add(new String[]{"Logical", "Interrogate"});
        add(new String[]{"Ethical", "Interrogate"});
        add(new String[]{"Interrogate", "Intimidate"});
    }};

    public Socrates( boolean isPlayer,
                     int board_x,
                     int board_y,
                     int screen_width,
                     BitmapFont font,
                     Texture socrates_tex,
                     Texture mini_tex) {
        super(board_x, board_y, log_stats, eth_stats, inm_stats, ing_stats, LOG_MAX, ETH_MAX, INT_MAX, ING_MAX, isPlayer);

        // Set up Socrates' combos
        this.set_combo(new Combo(combo_list));

        Portrait portrait = new Portrait(this, SympGame.manager.get("img/yi-combos.png", Texture.class), -60, 0, 400, 292, screen_width/2 - 250, 700, 220, 250, 500, "Socrates\n" +
                "Nobody fucks with Socrates and gets away with it.\n" +
                "\n" +
                "Nobody.\n" +
                "\n" +
                "Socrates' passive ability SOCRATIC METHOD reduces all confidence damage taken by 20%");
        portrait.setImg(socrates_tex);

        //portrait.setContestant(yi);
        this.set_portrait(portrait);

        //yi = new Contestant(combos, BOARD_WIDTH, BOARD_HEIGHT, log_stats, eth_stats, inm_stats, ing_stats, 100, 140, screen_width/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 120, true, current_board.cells[BOARD_WIDTH - 1][BOARD_HEIGHT - 1]);
        setImg(mini_tex);

        // Init AbilityList abilities here
        AbilityList.init_abilities(this, get_abilities(), font);

        Ability.setup_ability_display(get_abilities(), screen_width);

    }
}

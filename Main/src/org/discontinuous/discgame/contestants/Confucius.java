package org.discontinuous.discgame.contestants;

import com.badlogic.gdx.Gdx;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.Board;
import org.discontinuous.discgame.Combo;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 4/4/14.
 */
public class Confucius extends Contestant {
    Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
        put("power", 60);
        put("conf_plus", 0);
        put("conf_minus", 10);
        put("ins_plus", 10);
        put("ins_minus", 0);
    }};
    Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
        put("power", 40);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 0);
        put("ins_minus", 0);
    }};
    Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
        put("power", 80);
        put("conf_plus", 0);
        put("conf_minus", 20);
        put("ins_plus", 0);
        put("ins_minus", 10);
    }};
    Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
        put("power", 120);
        put("conf_plus", 30);
        put("conf_minus", 20);
        put("ins_plus", 30);
        put("ins_minus", 10);
    }};

    // Set up Arlene's combos
    ArrayList<String []> arlene_combo_list = new  ArrayList<String []>();
    arlene_combo_list.add(new String[]{"Intimidate","Logical"});
    arlene_combo_list.add(new String[]{"Logical", "Ethical"});
    arlene_combo_list.add(new String[]{"Interrogate", "Ethical"});
    arlene_combo_list.add(new String[]{"Interrogate", "Logical"});
    arlene_combo_list.add(new String[]{"Interrogate", "Intimidate"});
    arlene_combo_list.add(new String[]{"Intimidate", "Ethical"});
    Combo arlene_combo = new Combo(arlene_combo_list);

    arlene = new Contestant(arlene_combo, 1, 1, log_stats, eth_stats, inm_stats, ing_stats, 200, 200, screen_width/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 40, false, current_board.cells[0][0]);
    arlene.setImg(new Texture(Gdx.files.internal("img/arlenemini.png")));
    //arlene.img.scale((float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE - 1);
    arlene_portrait.setContestant(arlene);
}

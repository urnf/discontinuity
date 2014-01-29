package org.discontinuous.discgame;

import java.util.ArrayList;

/**
 * Created by Urk on 1/22/14.
 */
public class Combo {
    ArrayList<String []> combo_list;

    public Combo (ArrayList combo_list) {
        this.combo_list = combo_list;
    }

    public boolean checkCombo (Cell begin, Cell end) {
        // UGH.  I should not have to write my own string tuple comparator here.  I'm doing something wrong.
        for (String[] possible_combo : combo_list) {
            if (possible_combo[0] == begin.type.toString() && possible_combo[1] == end.type.toString() && !end.consumed) return true;
        }
        return false;
    }

    public void showCombo () {
        // TODO: animate "Combo" or stats floating up from player
    }
}

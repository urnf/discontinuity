package org.discontinuous.discgame;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by Urk on 1/21/14.
 */
public class Topic {
    String name;
    // TODO: Refactor this shit into arrays of arraylists.  Messy as all hell.
    ArrayList<String> yi_log_options;
    ArrayList<String> yi_eth_options;
    ArrayList<String> yi_ing_options;
    ArrayList<String> yi_inm_options;
    ArrayList<String> arlene_log_options;
    ArrayList<String> arlene_eth_options;
    ArrayList<String> arlene_ing_options;
    ArrayList<String> arlene_inm_options;

    public Topic (Object name, Object logical, Object ethical, Object interrogate, Object intimidate) {
        this.name = (String) name;
        yi_log_options = (ArrayList) (((LinkedHashMap) logical).get("Yi"));
        yi_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Yi"));
        yi_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Yi"));
        yi_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Yi"));
        arlene_log_options = (ArrayList) (((LinkedHashMap) logical).get("Arlene"));
        arlene_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Arlene"));
        arlene_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Arlene"));
        arlene_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Arlene"));
    }

    public String getYiDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (yi_log_options.size() == 0) { return "Debug: No more logical responses for Yi.  Congratulations, Yi is now illogical, you monster."; }
                return yi_log_options.remove(new Random().nextInt(yi_log_options.size()));
            case Ethical:
                if (yi_eth_options.size() == 0) { return "Debug: No more ethical responses for Yi.  Never let ethics get the best of him, anyhow."; }
                return yi_eth_options.remove(new Random().nextInt(yi_eth_options.size()));
            case Interrogate:
                if (yi_ing_options.size() == 0) { return "Debug: No more interrogate responses for Yi, which is impossible, since he always has a question."; }
                return yi_ing_options.remove(new Random().nextInt(yi_ing_options.size()));
            case Intimidate:
                if (yi_inm_options.size() == 0) { return "Debug: No more intimidate responses for Yi.  Intimidation is for the weak, anyway."; }
                return yi_inm_options.remove(new Random().nextInt(yi_inm_options.size()));
            default:
                return "Invalid option";
        }
    }

    public String getArleneDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (arlene_log_options.size() == 0) { return "Debug: No more logical responses for Arlene.  Dafuq?"; }
                return arlene_log_options.remove(new Random().nextInt(arlene_log_options.size()));
            case Ethical:
                if (arlene_eth_options.size() == 0) { return "Debug: No more ethical responses for Arlene.  You'd think that, but you'd be wrong."; }
                return arlene_eth_options.remove(new Random().nextInt(arlene_eth_options.size()));
            case Interrogate:
                if (arlene_ing_options.size() == 0) { return "Debug: No more interrogate responses for Arlene.  Except, she's already peeled info from your client. NOOOOOOOOOooooo-"; }
                return arlene_ing_options.remove(new Random().nextInt(arlene_ing_options.size()));
            case Intimidate:
                if (arlene_inm_options.size() == 0) { return "Debug: No more intimidate responses for Arlene.  Nevermore's more than enough."; }
                return arlene_inm_options.remove(new Random().nextInt(arlene_inm_options.size()));
            default:
                return "Invalid option";
        }
    }
}

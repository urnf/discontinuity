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
    ArrayList<String> yi_resp_log_options;
    ArrayList<String> yi_resp_eth_options;
    ArrayList<String> yi_resp_ing_options;
    ArrayList<String> yi_resp_inm_options;
    ArrayList<String> arlene_resp_log_options;
    ArrayList<String> arlene_resp_eth_options;
    ArrayList<String> arlene_resp_ing_options;
    ArrayList<String> arlene_resp_inm_options;
    int choice;

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
        yi_resp_log_options = (ArrayList) (((LinkedHashMap) logical).get("Yi_Resp"));
        yi_resp_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Yi_Resp"));
        yi_resp_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Yi_Resp"));
        yi_resp_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Yi_Resp"));
        arlene_resp_log_options = (ArrayList) (((LinkedHashMap) logical).get("Arlene_Resp"));
        arlene_resp_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Arlene_Resp"));
        arlene_resp_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Arlene_Resp"));
        arlene_resp_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Arlene_Resp"));
    }

    public String[] getYiDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (yi_log_options.size() == 0) { return new String[] {
                        "Debug: No more logical responses for Yi.  Congratulations, Yi is now illogical, you monster.",
                        "Debug: No more logical rebuttals for Arlene."
                }; }
                choice = new Random().nextInt(yi_log_options.size());
                return new String[]{yi_log_options.remove(choice), arlene_resp_log_options.remove(choice)};
            case Ethical:
                if (yi_eth_options.size() == 0) { return new String[] {
                        "Debug: No more ethical responses for Yi.  Never let ethics get the best of him, anyhow.",
                        "Debug: No more ethical rebuttals for Arlene."
                }; }
                choice = new Random().nextInt(yi_eth_options.size());
                return new String[]{yi_eth_options.remove(choice), arlene_resp_eth_options.remove(choice)};
            case Interrogate:
                if (yi_ing_options.size() == 0) { return new String[] {
                        "Debug: No more interrogate responses for Yi, which is impossible, since he always has a question.",
                        "Debug: No more interrogate rebuttals for Arlene."
                }; }
                choice = new Random().nextInt(yi_ing_options.size());
                return new String[]{yi_ing_options.remove(choice), arlene_resp_ing_options.remove(choice)};
            case Intimidate:
                if (yi_inm_options.size() == 0) { return new String[] {
                        "Debug: No more intimidate responses for Yi.  Intimidation is for the weak, anyway.",
                        "Debug: No more intimidate rebuttals for Arlene."
                }; }
                choice = new Random().nextInt(yi_inm_options.size());
                return new String[]{yi_inm_options.remove(choice), arlene_resp_inm_options.remove(choice)};
            default:
                return new String[]{"Invalid option", "Invalid option"};
        }
    }

    public String[] getArleneDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (arlene_log_options.size() == 0) { return new String[] {
                        "Debug: No more logical responses for Arlene.  Dafuq?",
                        "Debug: No more logical rebuttals for Yi."
                }; }
                choice = new Random().nextInt(arlene_log_options.size());
                return new String[]{arlene_log_options.remove(choice), yi_resp_log_options.remove(choice)};
            case Ethical:
                if (arlene_eth_options.size() == 0) { return new String[] {
                        "Debug: No more ethical responses for Arlene.  You'd think that, but you'd be wrong.",
                        "Debug: No more ethical rebuttals for Yi."
                }; }
                choice = new Random().nextInt(arlene_eth_options.size());
                return new String[]{arlene_eth_options.remove(choice), yi_resp_eth_options.remove(choice)};
            case Interrogate:
                if (arlene_ing_options.size() == 0) { return new String[] {
                        "Debug: No more interrogate responses for Arlene.  Except, she's already peeled info from your client. NOOOOOOOOOooooo-",
                        "Debug: No more interrogate rebuttals for Yi."
                }; }
                choice = new Random().nextInt(arlene_ing_options.size());
                return new String[]{arlene_ing_options.remove(choice), yi_resp_ing_options.remove(choice)};
            case Intimidate:
                if (arlene_inm_options.size() == 0) { return new String[] {
                        "Debug: No more intimidate responses for Arlene.  Nevermore's more than enough.",
                        "Debug: No more intimidate rebuttals for Yi."
                }; }
                choice = new Random().nextInt(arlene_inm_options.size());
                return new String[]{arlene_inm_options.remove(choice), yi_resp_inm_options.remove(choice)};
            default:
                return new String[] {"Invalid option", "Invalid option"};
        }
    }
}
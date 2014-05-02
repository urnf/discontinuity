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
    ArrayList<String> player_log_options;
    ArrayList<String> player_eth_options;
    ArrayList<String> player_ing_options;
    ArrayList<String> player_inm_options;
    ArrayList<String> computer_log_options;
    ArrayList<String> computer_eth_options;
    ArrayList<String> computer_ing_options;
    ArrayList<String> computer_inm_options;
    /*
    OBSOLETE
    ArrayList<String> yi_resp_log_options;
    ArrayList<String> yi_resp_eth_options;
    ArrayList<String> yi_resp_ing_options;
    ArrayList<String> yi_resp_inm_options;
    ArrayList<String> arlene_resp_log_options;
    ArrayList<String> arlene_resp_eth_options;
    ArrayList<String> arlene_resp_ing_options;
    ArrayList<String> arlene_resp_inm_options;
    */
    int choice;

    public Topic (Object name, Object logical, Object ethical, Object interrogate, Object intimidate) {
        this.name = (String) name;
        String player = DiscGame.player.getClass().getSimpleName();
        String computer = DiscGame.computer.getClass().getSimpleName();
        player_log_options = (ArrayList) (((LinkedHashMap) logical).get(player));
        player_eth_options = (ArrayList) (((LinkedHashMap) ethical).get(player));
        player_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get(player));
        player_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get(player));
        computer_log_options = (ArrayList) (((LinkedHashMap) logical).get(computer));
        computer_eth_options = (ArrayList) (((LinkedHashMap) ethical).get(computer));
        computer_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get(computer));
        computer_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get(computer));

        /*
        yi_resp_log_options = (ArrayList) (((LinkedHashMap) logical).get("Yi_Resp"));
        yi_resp_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Yi_Resp"));
        yi_resp_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Yi_Resp"));
        yi_resp_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Yi_Resp"));
        arlene_resp_log_options = (ArrayList) (((LinkedHashMap) logical).get("Arlene_Resp"));
        arlene_resp_eth_options = (ArrayList) (((LinkedHashMap) ethical).get("Arlene_Resp"));
        arlene_resp_ing_options = (ArrayList) (((LinkedHashMap) interrogate).get("Arlene_Resp"));
        arlene_resp_inm_options = (ArrayList) (((LinkedHashMap) intimidate).get("Arlene_Resp"));*/
    }

    public String[] getPlayerDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (player_log_options.size() == 0) {
                    return new String[] {
                        "Debug: No more logical responses for player.",
                        "Debug: No more logical rebuttals for computer."
                }; }
                //choice = new Random().nextInt(player_log_options.size());
                return new String[]{player_log_options.remove((int)(Math.random() * player_log_options.size())), ""};
            case Ethical:
                if (player_eth_options.size() == 0) { return new String[] {
                        "Debug: No more ethical responses for player.",
                        "Debug: No more ethical rebuttals for computer."
                }; }
                choice = new Random().nextInt(player_eth_options.size());
                return new String[]{player_eth_options.remove(choice), ""};
            case Interrogate:
                if (player_ing_options.size() == 0) { return new String[] {
                        "Debug: No more interrogate responses for player.",
                        "Debug: No more interrogate rebuttals for computer."
                }; }
                choice = new Random().nextInt(player_ing_options.size());
                return new String[]{player_ing_options.remove(choice), ""};
            case Intimidate:
                if (player_inm_options.size() == 0) { return new String[] {
                        "Debug: No more intimidate responses for player.",
                        "Debug: No more intimidate rebuttals for computer."
                }; }
                choice = new Random().nextInt(player_inm_options.size());
                return new String[]{player_inm_options.remove(choice), ""};
            default:
                return new String[]{"Invalid option", "Invalid option"};
        }
    }

    public String[] getComputerDialog(Cell cell) {
        switch (cell.type) {
            case Logical:
                if (computer_log_options.size() == 0) { return new String[] {
                        "Debug: No more logical responses for computer.",
                        "Debug: No more logical rebuttals for player."
                }; }
                choice = new Random().nextInt(computer_log_options.size());
                return new String[]{computer_log_options.remove(choice), ""};
            case Ethical:
                if (computer_eth_options.size() == 0) { return new String[] {
                        "Debug: No more ethical responses for computer.",
                        "Debug: No more ethical rebuttals for player."
                }; }
                choice = new Random().nextInt(computer_eth_options.size());
                return new String[]{computer_eth_options.remove(choice), ""};
            case Interrogate:
                if (computer_ing_options.size() == 0) { return new String[] {
                        "Debug: No more interrogate responses for computer",
                        "Debug: No more interrogate rebuttals for player."
                }; }
                choice = new Random().nextInt(computer_ing_options.size());
                return new String[]{computer_ing_options.remove(choice), ""};
            case Intimidate:
                if (computer_inm_options.size() == 0) { return new String[] {
                        "Debug: No more intimidate responses for computer",
                        "Debug: No more intimidate rebuttals for player."
                }; }
                choice = new Random().nextInt(computer_inm_options.size());
                return new String[]{computer_inm_options.remove(choice), ""};
            default:
                return new String[] {"Invalid option", "Invalid option"};
        }
    }
}

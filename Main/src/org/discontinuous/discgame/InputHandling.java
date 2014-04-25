package org.discontinuous.discgame;

import org.discontinuous.discgame.abilities.Ability;

/**
 * Created by urnf on 4/25/14.
 */
public class InputHandling {
    static Entity clicked;

    public static boolean tapClickHandle(int x, int y) {
        // If in dialog, advance to the next dialog or exit
        switch (StateHandling.currentState) {
            case InDialog:
                StateHandling.advanceDialog();
                return false;
            case AbilityDialog:
                StateHandling.currentSpeaker = DiscGame.player;
                StateHandling.advanceDialog();
                return false;
            case PostGameDialog:
                StateHandling.setup_endgame_options();
                StateHandling.currentState = StateHandling.State.PostGameSelect;
                return false;
        }

        // Loop over everything in the clickable list and see if it's being clicked.
        for (Entity e : DiscGame.click_list) {
            if (e.checkArea(x, y)) {clicked = e;}
        }
        // If no ability clicked or nothing clicked while in ability select, return to select Dialog
        if (StateHandling.checkState(StateHandling.State.SelectAbility) && (null == clicked || clicked.getClass() != Ability.class)) {
            StateHandling.currentState = StateHandling.State.SelectDialog;
            Ability.remove_ability_response(DiscGame.hover_list, DiscGame.click_list, DiscGame.player.abilities);
            return false;
        }
        // If no cell clicked or nothing clicked while in ability target, return to select ability
        if (StateHandling.checkState(StateHandling.State.AbilityTargeting) && (null == clicked || clicked.getClass() != Cell.class)) {
            StateHandling.currentState = StateHandling.State.SelectAbility;
            Ability.add_ability_response(DiscGame.hover_list, DiscGame.click_list, DiscGame.player.abilities);
            return false;
        }
        if (null != clicked) {clicked.clickHandler(); clicked = null;}
        return false;
    }
}

package org.discontinuous.discgame;

import com.badlogic.gdx.InputProcessor;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 12/19/13.
 */
public class DialogProcessor implements InputProcessor {
    boolean hovered;
    Entity clicked;

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        // If in dialog, advance to the next dialog or exit
        switch (StateHandling.currentState) {
            case InDialog:
                StateHandling.advanceDialog();
                return false;
            case AbilityDialog:
                StateHandling.currentSpeaker = DiscGame.yi;
                StateHandling.advanceDialog();
                return false;
            case PostGameDialog:
                StateHandling.setup_endgame_options();
                StateHandling.currentState = State.PostGameSelect;
                return false;
        }

        // Loop over everything in the clickable list and see if it's being clicked.
        for (Entity e : DiscGame.click_list) {
            if (e.checkArea(x, y)) {clicked = e;}
        }
        // If no ability clicked or nothing clicked while in ability select, return to select Dialog
        if (StateHandling.checkState(State.SelectAbility) && (null == clicked || clicked.getClass() != Ability.class)) {
            StateHandling.currentState = State.SelectDialog;
            Ability.remove_ability_response();
            return false;
        }
        // If no cell clicked or nothing clicked while in ability target, return to select ability
        if (StateHandling.checkState(State.AbilityTargeting) && (null == clicked || clicked.getClass() != Cell.class)) {
            StateHandling.currentState = State.SelectAbility;
            Ability.add_ability_response();
            return false;
        }
        if (null != clicked) {clicked.clickHandler(); clicked = null;}
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (Entity e : DiscGame.hover_list) {
            if (e.checkHover(screenX, screenY)) {hovered = true;}
        }
        if (!hovered) {DiscGame.hover = DiscGame.empty_hover; DiscGame.shape_hover = DiscGame.empty_hover;}
        hovered = false;
        DiscGame.mouse_x = screenX;
        DiscGame.mouse_y = screenY;
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }
}

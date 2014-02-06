package org.discontinuous.discgame;

import com.badlogic.gdx.InputProcessor;

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
        if (State.checkState(State.states.InDialog)) {
            State.advanceDialog();
            return false;
        }
        if (State.checkState(State.states.AbilityDialog)) {
            State.currentSpeaker = DiscGame.yi;
            State.advanceDialog();
            return false;
        }
        // Loop over everything in the clickable list and see if it's being clicked.
        for (Entity e : DiscGame.click_list) {
            if (e.checkArea(x, y)) {clicked = e;}
        }
        // If no ability clicked or nothing clicked while in ability select, return to select Dialog
        if (State.checkState(State.states.SelectAbility) && (null == clicked || clicked.getClass() != Ability.class)) {
            State.currentState = State.states.SelectDialog;
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

package org.discontinuous.discgame;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by Urk on 12/19/13.
 */
public class DialogProcessor implements InputProcessor {
    boolean hovered;

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
        return InputHandling.tapClickHandle(x, y);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (Entity e : SympGame.hover_list) {
            if (e.checkHover(screenX, screenY)) {hovered = true;}
        }
        if (!hovered) {
            SympGame.hover = SympGame.empty_hover; SympGame.shape_hover = SympGame.empty_hover;}
        hovered = false;
        SympGame.mouse_x = screenX;
        SympGame.mouse_y = screenY;
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

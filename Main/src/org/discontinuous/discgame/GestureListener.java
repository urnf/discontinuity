package org.discontinuous.discgame;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by urnf on 4/17/14.
 * Every single one of these methods is needed by libgdx's Gesture detection as per interface
 */
public class GestureListener implements GestureDetector.GestureListener {
    final int sensitivity_x = 3000;
    final int sensitivity_y = 3000;
    //Object selectedObject = SympGame.player.cell;

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        SympGame.mouse_x = (int) x;
        SympGame.mouse_y = (int) y;
        InputHandling.tapClickHandle((int) x, (int) y);
        return false;
    }

    @Override
    // TODO: Both pan and fling are going to need to have gesture handling
    public boolean fling (float velocityX, float velocityY, int button) {

        // Think about
        // - sensitivity
        // - x & y simultaneous

        if (velocityX > sensitivity_x) {

        }
        if (velocityY > sensitivity_y) {

        }
        if (velocityX < sensitivity_x) {

        }
        if (velocityY < sensitivity_y) {

        }



        SympGame.mouse_x = 111111111;
        SympGame.mouse_y = 222222222;
        return false;
    }

    @Override
    public boolean pinch (Vector2 startFinger1, Vector2 startFinger2, Vector2 endFinger1, Vector2 endFinger2) {
        return false;
    }

    @Override
    // What does this do exactly?  Javadocs aren't exactly elucidating
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    // LOL @ javadocs.  No javadocs description.
    public boolean longPress (float x, float y) {
       return false;
    }

    @Override
    // DBF = Distance between fingers - this will only be relevant in this scope so don't worry about shitting naming.
    public boolean zoom (float DBFstart, float DBFend) {
        return false;
    }

    @Override
    // TODO: BOTH PAN AND FLING WILL NEED TO HAVE HANDLING LOGIC
    // - HOW TO DETERMINE DIFFERENCE BETWEEN PAN AND FLING?
    public boolean pan(float x, float y, float xdiff, float ydiff) {
        SympGame.mouse_x = 333333333;
        SympGame.mouse_y = 222222222;
        return false;
    }

}
package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Entity is currently used for storing position for mouseover and sprite
 * Presentation is coupled with the data at the moment, may separate out later
 */
public class Entity {
    // TODO: Make these not publically accessible, create methods instead for specific types of manipulation
    public int x;
    public int y;
    public int width;
    public int height;
    // Assign this to a generic error texture so that it's visible when unset and drawn
    public Sprite img = null;

    public Entity (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // NOTE: this is used to mark a cell as consumed
    // I'm assuming that although this is a strong reference, the garbage collector will pick the old one up
    // Can chain this method into the constructor since it returns this
    public Entity setImg(Texture img) {
        img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.img = new Sprite(img, width, height);
        this.img.setPosition(x, y);
        return this;
    }
    public void draw(SpriteBatch batch) {
        img.draw(batch);
    }
    // boolean since we want this to return for subclasses that use this for logic
    public boolean checkHover(int mousex, int mousey){
        // Check to see if the mouse is hovered over this element
        // If so, assign the static hover to this element
        if (checkArea(mousex, mousey)) {
            DiscGame.hover = this;
            DiscGame.shape_hover = this;
            return true;
        }
        return false;
    }
    // check mouse is over this entity
    public boolean checkArea(int mousex, int mousey){
        // Check if it's inside, fire the element's hover if so
        // Siiiiiiiigh.  Libgdx's input are zero'd at top left, instead of gfx bottom right.
        if (mousex > x + 5 &&
                mousex < (x + width + 5) &&
                mousey < (DiscGame.DESIRED_HEIGHT - y + 5) &&
                mousey > (DiscGame.DESIRED_HEIGHT - y - height - 5)) {
            return true;
        }
        return false;
    }
    public void drawHover(SpriteBatch batch) {
        //DiscGame.text_font_small.draw(batch, "X: " + Gdx.input.getX() + " Y: " + Gdx.input.getY() + " Hover item: " + DiscGame.hover.toString(), x, y + 30);
    }

    public void drawShapeHover(ShapeRenderer shapes) {

    }

    public void clickHandler() {

    }
}

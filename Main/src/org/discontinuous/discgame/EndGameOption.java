package org.discontinuous.discgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 2/7/14.
 */
public class EndGameOption extends Entity {
    public int dp_cost;
    public String option_text;
    public BitmapFont font;
    public String arlene_text;
    public String yi_text;
    static Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    static Color dark_green = new Color(0.0664f, 0.4336f, 0.1523f, 1);
    static Color dark_red = new Color(0.4336f, 0.1172f, 0.0664f, 1);

    public EndGameOption(int dp_cost, BitmapFont font, String option_text, String yi_text, String arlene_text) {
        super(0, 0, 0, 0);
        this.dp_cost = dp_cost;
        this.option_text = option_text;
        this.font = font;
        this.yi_text = yi_text;
        this.arlene_text = arlene_text;
    }

    public void drawShapeHover(ShapeRenderer shapes) {
            // if backtracking, highlight red
            if (DiscGame.dealpower.dp < dp_cost) { shapes.setColor(dark_red); }
            // if can combo into this option, highlight green
            else if (DiscGame.dealpower.dp > dp_cost) { shapes.setColor(dark_green); }
            shapes.rect(x - 6, y - 6, width + 12, height + 12);
            shapes.setColor(dark_grey);
            shapes.rect(x, y, width, height);
    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.movestats_font.drawWrapped(batch, Integer.toString(dp_cost), x + 10, y + 75, width - 40);
        font.drawWrapped(batch, option_text, x + 70, y + 75, width - 70);
    }

    public void clickHandler() {
        if (DiscGame.dealpower.dp > dp_cost) {
            StateHandling.set_player_offset(yi_text);
            StateHandling.set_computer_offset(arlene_text);
            StateHandling.selected_endgame_option = this;

            for (EndGameOption option: DiscGame.endgame_options) {
                // Entity has action on hover, add to hover list
                DiscGame.hover_list.remove(option);
                // Entity may have action on click, add to click list
                DiscGame.click_list.remove(option);
            }
            DiscGame.hover_list.remove(DiscGame.player.portrait);
            DiscGame.hover_list.remove(DiscGame.computer.portrait);

            // Refresh mouse moved to get rid of annoying mouseover
            // TODO: Hacky as heck, fix
            Gdx.input.getInputProcessor().mouseMoved(0, 0);

            // TODO: Also hacky, image change for -9999 option
            if (dp_cost == -9999) {
                DiscGame.computer.portrait.x = DiscGame.screen_width - 382;
                DiscGame.computer.portrait.width = 382;
                DiscGame.computer.portrait.height = 700;
                DiscGame.player.portrait.x = 0;
                DiscGame.player.portrait.width = 382;
                DiscGame.player.portrait.height = 700;
                DiscGame.player.portrait.setImg(DiscGame.manager.get("img/YiDemonResize.png", Texture.class));
                DiscGame.computer.portrait.setImg(DiscGame.manager.get("img/ArleneLichResize.png", Texture.class));


            }

            StateHandling.currentState = State.PostGameResult;
        }
    }
}

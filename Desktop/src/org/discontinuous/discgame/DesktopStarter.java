package org.discontinuous.discgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created by Urk on 12/16/13.
 */
public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Symposium";
        cfg.useGL20 = true;
        cfg.width = 960;
        cfg.height = 540;
        cfg.resizable = true;
        new LwjglApplication(new SympGame(), cfg);
        DialogProcessor inputProcessor = new DialogProcessor();
        Gdx.input.setInputProcessor(inputProcessor);
    }
}

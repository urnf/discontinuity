package org.discontinuous.discgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created by Urk on 12/16/13.
 */
public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Discontinuity";
        cfg.useGL20 = true;
        cfg.width = 1920;
        cfg.height = 1080;
        cfg.resizable = true;
        new LwjglApplication(new DiscGame(), cfg);
    }
}

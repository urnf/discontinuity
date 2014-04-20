package org.discontinuous.discgame;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.input.GestureDetector;


public class AndroidStarter extends AndroidApplication
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useWakelock = true;
        cfg.useGL20 = true;
        initialize(new DiscGame(), cfg);
        // TODO: Fix the gesture detection values here later
        GestureDetector inputProcessor = new GestureDetector(new GestureListener());
        Gdx.input.setInputProcessor(inputProcessor);
    }
}

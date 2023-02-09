package com.csi4999.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AppPreferences {
    private static final String PREF_MASTER_VOL = "master";
    private static final String PREF_MUSIC_VOLUME = "music";
    private static final String PREF_SOUND_VOL = "sound";
    private static final String PREF_HUD_SCALE = "hud";
    private static final String PREF_FULLSCREEN = "fullscreen.enabled";
    private static final String PREFS_NAME = "alifeprefs";

    protected Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public float getMasterVolume() {
        return getPreferences().getFloat(PREF_MASTER_VOL, 0.5f);
    }

    public void setMasterVolume(float volume) {
        getPreferences().putFloat(PREF_MASTER_VOL, volume);
        getPreferences().flush();
    }

    public float getMusicVolume() {
        return getPreferences().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        getPreferences().putFloat(PREF_MUSIC_VOLUME, volume);
        getPreferences().flush();
    }

    public float getSoundVolume() {
        return getPreferences().getFloat(PREF_SOUND_VOL, 0.5f);
    }

    public void setSoundVolume(float volume) {
        getPreferences().putFloat(PREF_SOUND_VOL, volume);
        getPreferences().flush();
    }

    public float getHudScale() {
        return getPreferences().getFloat(PREF_HUD_SCALE, 0.5f);
    }

    public void setHudScale(float scale) {
        getPreferences().putFloat(PREF_HUD_SCALE, scale);
        getPreferences().flush();
    }

    public boolean isFullscreenEnabled() {
        return getPreferences().getBoolean(PREF_FULLSCREEN, true);
    }

    public void setFullscreenEnabled(boolean fullscreenEnabled) {
        getPreferences().putBoolean(PREF_FULLSCREEN, fullscreenEnabled);
        getPreferences().flush();
    }

}

package com.csi4999.systems.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.CreatureSpawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class CreatureNameTag {
    private Map<Long, String> chunkIDToName = new HashMap<>();
    private CreatureSpawner creatureSpawner;
    private BitmapFont font;

    public CreatureNameTag(List<Long> chunkIDs, List<String> usernames, CreatureSpawner creatureSpawner) {
        this.creatureSpawner = creatureSpawner;
        for (int i = 0; i < chunkIDs.size(); i++)
            chunkIDToName.put(chunkIDs.get(i), usernames.get(i));

        font = CustomAssetManager.getInstance().manager.get(UI_FONT);
    }

    public void render(Batch batch) {
        // TODO: cull?
        creatureSpawner.creatureLock.lock();
        for (Creature c : creatureSpawner.getCreatures()) {
            if (chunkIDToName.containsKey(c.chunkID)) {
                font.draw(batch, chunkIDToName.get(c.chunkID), c.position.x, c.position.y, 0, Align.center, false);
            }
        }
        creatureSpawner.creatureLock.unlock();
    }
}
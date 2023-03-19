package com.csi4999.systems.tournament;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.wrappers.Chunk;

import java.util.List;

import static com.csi4999.systems.ui.ChunkSelector.CHUNK_SIZE;

public class FuseEnvs {
    private static final int CHUNK_SEPARATION = CHUNK_SIZE * 2;

    public static void fuseEnvs(List<Environment> envs, Environment baseEnv) {
        if (envs.size() != 4)
            throw new RuntimeException("fuseEnvs only works with 4 environments!");

        // four corners
        envs.get(0).physics.shiftObjects(new Vector2(CHUNK_SEPARATION, CHUNK_SEPARATION));
        envs.get(1).physics.shiftObjects(new Vector2(-CHUNK_SEPARATION, CHUNK_SEPARATION));
        envs.get(2).physics.shiftObjects(new Vector2(CHUNK_SEPARATION, -CHUNK_SEPARATION));
        envs.get(3).physics.shiftObjects(new Vector2(-CHUNK_SEPARATION, -CHUNK_SEPARATION));

        for (Environment env : envs)
            baseEnv.merge(env);
    }
}

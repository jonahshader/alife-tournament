package com.csi4999.systems.tournament;

import com.csi4999.screens.SimScreen;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.common.ChunkPerformance;
import com.csi4999.systems.networking.packets.TournamentResultsPacket;

import java.util.*;
import java.util.stream.Collectors;

public class WinCondition {
    private SimScreen sim;
    public static final long TIME_LIMIT =  (long) (5 * 60 / Environment.dt); // 5 minutes (sim time)
    private List<Long> originalChunkIDs;
    private boolean gameDone = false;



    public WinCondition(SimScreen sim, List<Long> originalChunkIDs) {
        this.sim = sim;
        this.originalChunkIDs = originalChunkIDs;
    }

    public void update() {
        if (!gameDone) {
            if (timeoutCondition() || soleSurvivorOrDeadCondition()) {
                evaluate();
                gameDone = true;
                sim.toolBar.setPlayState(false);
            }
        }
    }

    private boolean timeoutCondition() {
        return sim.env.age >= TIME_LIMIT;
    }

    private boolean soleSurvivorOrDeadCondition() {
        Set<Long> chunkIDs = new HashSet<>();
        for (Creature c : sim.env.creatureSpawner.getCreatures())
            chunkIDs.add(c.chunkID);

        return chunkIDs.size() <= 1;
    }

    private void evaluate() {
        // tally up the creatures by chunkID
        Map<Long, Integer> chunkCounts = new HashMap<>();
        for (Creature c : sim.env.creatureSpawner.getCreatures()) {
            if (!chunkCounts.containsKey(c.chunkID))
                chunkCounts.put(c.chunkID, 0);
            chunkCounts.put(c.chunkID, chunkCounts.get(c.chunkID) + 1);
        }

        List<ChunkPerformance> chunkPerformances = new ArrayList<>();
        float total = sim.env.creatureSpawner.getCreatures().size();
        if (!chunkCounts.isEmpty()) {
            for (Long chunkID : originalChunkIDs) {
                if (chunkCounts.containsKey(chunkID))
                    chunkPerformances.add(new ChunkPerformance(chunkID, chunkCounts.get(chunkID) / total));
                else
                    chunkPerformances.add(new ChunkPerformance(chunkID, 0f));
            }

        } else {
            for (Long chunkID : originalChunkIDs) {
                chunkPerformances.add(new ChunkPerformance(chunkID, 1f/4)); // they got 1/4th of a win because they all died lol
            }
        }
        sim.tournamentResults = new TournamentResultsPacket(chunkPerformances);
        // send results to server
        GameClient.getInstance().client.sendTCP(sim.tournamentResults);
    }
}

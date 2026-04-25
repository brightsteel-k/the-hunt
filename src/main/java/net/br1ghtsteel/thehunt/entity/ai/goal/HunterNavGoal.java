package net.br1ghtsteel.thehunt.entity.ai.goal;

import net.minecraft.entity.ai.pathing.PathNode;

public interface HunterNavGoal {
    void onPathNodeCompleted(PathNode node);
}

package net.br1ghtsteel.thehunt.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class HunterAttackGoal extends MeleeAttackGoal {

    public HunterAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }
}

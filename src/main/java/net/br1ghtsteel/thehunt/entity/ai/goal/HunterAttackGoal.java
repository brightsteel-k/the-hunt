package net.br1ghtsteel.thehunt.entity.ai.goal;

import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class HunterAttackGoal extends MeleeAttackGoal {

    AbstractHunterEntity hunterEntity;

    public HunterAttackGoal(AbstractHunterEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.hunterEntity = mob;
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double)(hunterEntity.getReachDistance() * hunterEntity.getReachDistance() + entity.getWidth());
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.hunterEntity.canSee(target)) {
            super.attack(target, squaredDistance);
        }
    }
}

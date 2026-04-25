package net.br1ghtsteel.thehunt.entity.ai.control;

import net.br1ghtsteel.thehunt.TheHunt;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;

public class HunterMoveControl extends MoveControl {
    public HunterMoveControl(MobEntity entity) {
        super(entity);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void moveTo(double x, double y, double z, double speed) {
        super.moveTo(x, y, z, speed);
    }
}

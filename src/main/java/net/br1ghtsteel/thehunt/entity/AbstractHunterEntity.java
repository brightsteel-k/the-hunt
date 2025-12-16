package net.br1ghtsteel.thehunt.entity;

import net.br1ghtsteel.thehunt.entity.ai.pathing.HunterNavigation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class AbstractHunterEntity extends HostileEntity {

    public AbstractHunterEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.BLOCKED, 64.0F);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new HunterNavigation(this, world);
    }
}

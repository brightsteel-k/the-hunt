package net.br1ghtsteel.thehunt.entity;

import com.google.common.collect.Maps;
import net.br1ghtsteel.thehunt.entity.ai.pathing.HunterNavigation;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AbstractHunterEntity extends HostileEntity {

    protected float miningSpeed;
    protected float reachDistance;

    public AbstractHunterEntity(EntityType<? extends HostileEntity> entityType, World world, float miningSpeed, float reachDistance) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.BREACH, 32.0F);
        this.miningSpeed = miningSpeed;
        this.reachDistance = reachDistance;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new HunterNavigation(this, world);
    }

    public float getMiningSpeed() {
        return this.miningSpeed;
    }

    public float getReachDistance() {
        return this.reachDistance;
    }

    @Nullable
    @Override
    public EntityData initialize(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
    ) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        return entityData;
    }
}

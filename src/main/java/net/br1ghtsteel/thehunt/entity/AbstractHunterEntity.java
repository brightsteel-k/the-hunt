package net.br1ghtsteel.thehunt.entity;

import com.google.common.collect.Maps;
import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.ai.control.HunterMoveControl;
import net.br1ghtsteel.thehunt.entity.ai.pathing.HunterNavigation;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        this.miningSpeed = miningSpeed;
        this.reachDistance = reachDistance;
        this.moveControl = new HunterMoveControl(this);

        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.BREACH, 32.0F);       // OBSTRUCTED
        this.setPathfindingPenalty(PathNodeType.STICKY_HONEY, 12.0F); // OBSTRUCTED_SINGLE
        this.setPathfindingPenalty(PathNodeType.LEAVES, 32.0F);       // BUILDABLE
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

    public void jumpToPos(Vec3d targetPos) {
        Vec3d d = targetPos.subtract(this.getPos());
        if (d.horizontalLengthSquared() > 400) {
            return;
        }
        double dy = d.getY();
        double vy = this.getJumpVelocity();
        double acc = -0.04;

        double det = (vy * vy) + (4.0 * acc * dy);
        double jumpTime = (-vy - Math.sqrt(det)) / (2.0 * acc);
        double vx = (d.horizontalLength() * 1.2) / jumpTime;

        d = d.normalize();
        this.setVelocity(d.x * vx, vy, d.z * vx);
        this.velocityDirty = true;
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

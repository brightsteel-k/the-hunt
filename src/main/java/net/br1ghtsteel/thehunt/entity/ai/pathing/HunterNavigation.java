package net.br1ghtsteel.thehunt.entity.ai.pathing;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class HunterNavigation extends EntityNavigation {

    private AbstractHunterEntity hunterEntity;
    private DustColorTransitionParticleEffect nodeWalkMarker;
    private DustColorTransitionParticleEffect nodeBreakMarker;
    private boolean showDebugPath = true;

    public HunterNavigation(AbstractHunterEntity hunterEntity, World world) {
        super(hunterEntity, world);
        this.hunterEntity = hunterEntity;
        this.nodeWalkMarker  = new DustColorTransitionParticleEffect(
                new Vector3f(235F/255F, 26F/255F, 255F/255F),
                new Vector3f(0.2F, 0.2F, 0.2F),
                1.0F
        );
        this.nodeBreakMarker  = new DustColorTransitionParticleEffect(
                new Vector3f(255F/255F, 148F/255F, 50F/255F),
                new Vector3f(0.2F, 0.2F, 0.2F),
                1.0F
        );
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new HunterPathNodeMaker();
        this.nodeMaker.setCanWalkOverFences(false);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.isOnGround() || this.isInLiquid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), (double)this.getPathfindingY(), this.entity.getZ());
    }

    @Override
    public Path findPathTo(BlockPos target, int distance) {
        if (this.world.getBlockState(target).isAir()) {
            BlockPos blockPos = target.down();

            while (blockPos.getY() > this.world.getBottomY() && this.world.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.down();
            }

            if (blockPos.getY() > this.world.getBottomY()) {
                return super.findPathTo(blockPos.up(), distance);
            }

            while (blockPos.getY() < this.world.getTopY() && this.world.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.up();
            }

            target = blockPos;
        }

        if (!this.world.getBlockState(target).isSolid()) {
            return super.findPathTo(target, distance);
        } else {
            BlockPos blockPos = target.up();

            while (blockPos.getY() < this.world.getTopY() && this.world.getBlockState(blockPos).isSolid()) {
                blockPos = blockPos.up();
            }

            return super.findPathTo(blockPos, distance);
        }
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getBlockPos(), distance);
    }

    /**
     * The y-position to act as if the entity is at for pathfinding purposes
     */
    private int getPathfindingY() {
        if (this.entity.isTouchingWater() && this.canSwim()) {
            int i = this.entity.getBlockY();
            BlockState blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)i, this.entity.getZ()));
            int j = 0;

            while (blockState.isOf(Blocks.WATER)) {
                blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)(++i), this.entity.getZ()));
                if (++j > 16) {
                    return this.entity.getBlockY();
                }
            }

            return i;
        } else {
            return MathHelper.floor(this.entity.getY() + 0.5);
        }
    }

    @Override
    protected void adjustPath() {
        super.adjustPath();
        if (this.entity.getTarget() != null) {
            LivingEntity target = this.entity.getTarget();
            for (int i = 0; i < this.currentPath.getLength(); i++) {
                PathNode pathNode = this.currentPath.getNode(i);
                Vec3d nodePos = new Vec3d(pathNode.x, pathNode.y, pathNode.z);
                Vec3d targetPos = target.getPos().add(target.getVelocity().multiply(3.0F));
                if (targetPos.isInRange(nodePos, this.hunterEntity.getReachDistance() - 1.6f)) {
                    this.currentPath.setLength(i);
                    return;
                }
            }
        }
    }

    protected boolean canWalkOnPath(PathNodeType pathType) {
        if (pathType == PathNodeType.WATER) {
            return false;
        } else {
            return pathType == PathNodeType.LAVA ? false : pathType != PathNodeType.OPEN;
        }
    }

    @Override
    public boolean startMovingAlong(@Nullable Path path, double speed) {
        if (!(this.world instanceof ServerWorld serverWorld)) {
            return super.startMovingAlong(path, speed);
        }

        if (this.showDebugPath && path != null) {
            for (int i = 0; i < path.getLength(); i++) {
                PathNodeType pathNodeType = path.getNode(i).type;
                Vec3d pathNodePos = path.getNode(i).getPos();
                serverWorld.spawnParticles(
                        pathNodeType == PathNodeType.BREACH ? this.nodeBreakMarker : this.nodeWalkMarker,
                        pathNodePos.getX() + 0.5f,
                        pathNodePos.getY() + 0.2f,
                        pathNodePos.getZ() + 0.5f,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        1.0
                );
            }
        }
        return super.startMovingAlong(path, speed);
    }
}

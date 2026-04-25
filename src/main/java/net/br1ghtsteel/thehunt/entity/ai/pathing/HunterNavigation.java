package net.br1ghtsteel.thehunt.entity.ai.pathing;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.br1ghtsteel.thehunt.entity.ai.goal.HunterNavGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public class HunterNavigation extends EntityNavigation {

    private final AbstractHunterEntity hunterEntity;
    private final Map<PathNodeType, DustColorTransitionParticleEffect> nodeMarkers = new HashMap<>();
    private final boolean showDebugPath = true;
    private final List<HunterNavGoal> registeredGoals = new ArrayList<>();

    public HunterNavigation(AbstractHunterEntity hunterEntity, World world) {
        super(hunterEntity, world);
        this.hunterEntity = hunterEntity;
        if (showDebugPath) {
            registerDebugNodeMarker(PathNodeType.OPEN, new Vector3f(235F/255F, 26F/255F, 255F/255F));
            registerDebugNodeMarker(PathNodeType.BREACH, new Vector3f(255F/255F, 208F/255F, 48F/255F));
            registerDebugNodeMarker(PathNodeType.STICKY_HONEY, new Vector3f(255F/255F, 208F/255F, 48F/255F));
            registerDebugNodeMarker(PathNodeType.LEAVES, new Vector3f(30F/255F, 219F/255F, 25F/255F));
        }
    }

    protected void registerDebugNodeMarker(PathNodeType nodeType, Vector3f color) {
        this.nodeMarkers.put(nodeType, new DustColorTransitionParticleEffect(
            color, new Vector3f(0.2F, 0.2F, 0.2F), 1.0F
        ));
    }

    public void registerGoal(HunterNavGoal goal) {
        registeredGoals.add(goal);
    }

    public void unregisterGoal(HunterNavGoal goal) {
        registeredGoals.remove(goal);
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
    protected void continueFollowingPath() {
        Vec3d vec3d = this.getPos();
        PathNode current = this.currentPath.getCurrentNode();
        this.nodeReachProximity = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
        Vec3i vec3i = current.getBlockPos();
        double d = Math.abs(this.entity.getX() - ((double)vec3i.getX() + 0.5));
        double e = Math.abs(this.entity.getY() - (double)vec3i.getY());
        double f = Math.abs(this.entity.getZ() - ((double)vec3i.getZ() + 0.5));
        boolean bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e < 1.0;
        if (bl || this.canJumpToNext(this.currentPath.getCurrentNode().type) && this.shouldJumpToNextNode(vec3d)) {
            callPathNodeCompleted(current);
            this.currentPath.next();
        }

        this.checkTimeouts(vec3d);
    }

    protected void callPathNodeCompleted(PathNode node) {
        for (HunterNavGoal goal : registeredGoals) {
            goal.onPathNodeCompleted(node);
        }
    }

    protected boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) {
            return false;
        } else {
            Vec3d vec3d = Vec3d.ofBottomCenter(this.currentPath.getCurrentNodePos());
            if (!currentPos.isInRange(vec3d, 2.0)) {
                return false;
            } else if (this.canPathDirectlyThrough(currentPos, this.currentPath.getNodePosition(this.entity))) {
                return true;
            } else {
                Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.getNodePos(this.currentPath.getCurrentNodeIndex() + 1));
                Vec3d vec3d3 = vec3d.subtract(currentPos);
                Vec3d vec3d4 = vec3d2.subtract(currentPos);
                double d = vec3d3.lengthSquared();
                double e = vec3d4.lengthSquared();
                boolean bl = e < d;
                boolean bl2 = d < 0.5;
                if (!bl && !bl2) {
                    return false;
                } else {
                    Vec3d vec3d5 = vec3d3.normalize();
                    Vec3d vec3d6 = vec3d4.normalize();
                    return vec3d6.dotProduct(vec3d5) < 0.0;
                }
            }
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
                        nodeMarkers.getOrDefault(pathNodeType, nodeMarkers.get(PathNodeType.OPEN)),
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

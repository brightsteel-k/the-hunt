package net.br1ghtsteel.thehunt.entity;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.ai.goal.BreakBlockOnPathGoal;
import net.br1ghtsteel.thehunt.entity.ai.goal.HunterAttackGoal;
import net.br1ghtsteel.thehunt.entity.ai.goal.JumpGapOnPathGoal;
import net.br1ghtsteel.thehunt.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class TorivorEntity extends AbstractHunterEntity {

    public TorivorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, 2.0F, 3.0F);
    }

    public static DefaultAttributeContainer.Builder createTorivorAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.38F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new BreakBlockOnPathGoal(this));
        this.goalSelector.add(2, new JumpGapOnPathGoal(this, 4, 2, Blocks.COBBLED_DEEPSLATE));
        this.goalSelector.add(3, new HunterAttackGoal(this, 1, true));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, VillagerEntity.class, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.TORIVORIAN_SWORD));
    }
}

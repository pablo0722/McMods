package net.mcreator.pruebamod.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.util.Mth;

import net.minecraft.tags.BlockTags;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.phys.Vec3;

import net.mcreator.pruebamod.init.PruebaModModEntities;

import net.minecraft.world.level.Level;

public abstract class CustomAnimal extends Animal {
	protected List<Behaviour> behaviours = null;

	abstract public void initBehaviours();

    public boolean preferFalse(boolean a, boolean b)
    {
        return a && b;
    }

    public boolean preferTrue(boolean a, boolean b)
    {
        return a || b;
    }

	public CustomAnimal(EntityType<? extends CustomAnimal> p_28285_, Level p_28286_) {
		super(p_28285_, p_28286_);
    }

    public void setMoveControl(MoveControl moveControl) {
        this.moveControl = moveControl;
    }

	@Override
	protected void registerGoals() {
		initBehaviours();
		for (Behaviour b: this.behaviours) {
			b.registerGoalPartial();
		}
	}

	@Override
	protected boolean isFlapping() {
		// called from entity.java

      	boolean ret = false;
        
		initBehaviours();

        ret = preferTrue(ret, super.isFlapping());

		for (Behaviour b: this.behaviours) {
			ret = preferTrue(ret, b.isFlappingPartial());
		}

		return ret;
	}

	@Override
	protected void onFlap() {
		// called from entity.java

      	super.onFlap();

		initBehaviours();
		for (Behaviour b: this.behaviours) {
			b.onFlapPartial();
		}
	}

	@Override
	public boolean causeFallDamage(float fallDistance, float d, DamageSource fallDamageSource) {
      	boolean ret = true;
        //System.out.println("falldistance: " + Float.toString(this.fallDistance));

		initBehaviours();
		for (Behaviour b: this.behaviours) {
            ret = preferFalse(ret, b.causeFallDamagePartial(fallDistance, d, fallDamageSource));
		};

        if(ret) {
            ret = preferFalse(ret, super.causeFallDamage(fallDistance, d, fallDamageSource));
        }

		return ret;
	}

	@Nullable
	@Override
	public Cow getBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_) {
		return EntityType.COW.create(p_148890_);
	}

	@Override
	protected float getStandingEyeHeight(Pose p_28295_, EntityDimensions p_28296_) {
		return this.isBaby() ? p_28296_.height * 0.95F : 1.3F;
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	public boolean isFood(ItemStack p_29446_) {
		return false;
	}

	@Override
	public boolean canMate(Animal p_29381_) {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	protected void doPush(Entity p_29367_) {
		super.doPush(p_29367_);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.COW_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_28306_) {
		return SoundEvents.COW_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.COW_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos p_28301_, BlockState p_28302_) {
		this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
}
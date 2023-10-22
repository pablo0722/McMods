package net.mcreator.pruebamod.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;



public class FlyBehaviour implements Behaviour {
	/* Inherited variables
	* protected MoveControl moveControl; // Mob.java
	* public final GoalSelector goalSelector; // Mob.java
	* public float flyDist; // Entity,java
	*/
	
    public CustomAnimal This;
	public float flapSpeed = 1.0F;
	public float nextFlap = 1.0F;

   	public FlyBehaviour(CustomAnimal This) {
		this.This = This;

		// moveControl defined in Mob.java
		int maxDegreesPerTick = 1;
		boolean alwaysFlying = false;
		double flyHeight = 10.0F;
		double flyThresholdStartDistance = 5.0F;
		double flyThresholdEndDistance = 1.0F;
		This.setMoveControl(new FlyingDragonMoveControl(This, This.level, maxDegreesPerTick, alwaysFlying, flyHeight, flyThresholdStartDistance, flyThresholdEndDistance));
	}

	static public void createAttributesPartial(AttributeSupplier.Builder builder) {
		builder.add(Attributes.FLYING_SPEED, (double)0.4F);
	}
	
	@Override
	public void registerGoalPartial() {
		This.goalSelector.addGoal(0, new FloatGoal(This));
		This.goalSelector.addGoal(2, new BreedGoal(This, 1.0D));
		This.goalSelector.addGoal(4, new FollowParentGoal(This, 1.25D));
		This.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(This, 1.0D));
		This.goalSelector.addGoal(6, new LookAtPlayerGoal(This, Player.class, 6.0F));
		This.goalSelector.addGoal(7, new RandomLookAroundGoal(This));
	}

	@Override
	public boolean isFlappingPartial() {
		return false;//This.flyDist > this.nextFlap;
	}

	@Override
	public void onFlapPartial() {
		//This.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
		//this.nextFlap = This.flyDist + this.flapSpeed / 2.0F;
	}

	@Override
	public boolean causeFallDamagePartial(float l, float d, DamageSource source) {
		return false;
	}
}
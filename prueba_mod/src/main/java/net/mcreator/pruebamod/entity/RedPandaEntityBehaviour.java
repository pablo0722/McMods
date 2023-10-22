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

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;

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


/*
package net.mcreator.pruebamod.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class RedPandaEntity extends RedPandaEntityBehaviour{

	public RedPandaEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(packet, world);
	}

	public RedPandaEntity(EntityType<RedPandaEntity> type, Level world) {
		super(type, world);
	}
}
*/

public class RedPandaEntityBehaviour extends CustomAnimal {
	public RedPandaEntityBehaviour(EntityType<? extends RedPandaEntityBehaviour> p_28285_, Level p_28286_) {
		super(p_28285_, p_28286_);
	}

	@Override
	public void initBehaviours() {
		if(this.behaviours == null) {
			this.behaviours = new ArrayList<>();
			
			this.behaviours.add(new FlyBehaviour(this));
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();

		builder.add(Attributes.MAX_HEALTH, 10.0D);
		builder.add(Attributes.MOVEMENT_SPEED, (double)0.2F);

		FlyBehaviour.createAttributesPartial(builder);

		return builder;
	}

	protected PathNavigation createNavigation(Level p_29417_) {
		FlyingDragonPathNavigation flyingDragonPathNavigation = new FlyingDragonPathNavigation(this, p_29417_);
		flyingDragonPathNavigation.setCanOpenDoors(false);
		flyingDragonPathNavigation.setCanFloat(true);
		flyingDragonPathNavigation.setCanPassDoors(true);
		return flyingDragonPathNavigation;
	}
}